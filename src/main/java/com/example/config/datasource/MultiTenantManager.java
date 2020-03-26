package com.example.config.datasource;

import com.example.utilities.Utils;
import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

@Component
@SuppressWarnings("unused")
public class MultiTenantManager implements InitializingBean, DisposableBean {

    private static final Logger log = LogManager.getLogger(MultiTenantManager.class);
    private final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();
    private AbstractRoutingDataSource multiTenantDataSource;
    private UniversalConnectionPoolManager mgr;
    private DataSource cds;

    {
        try {
            mgr = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
        } catch (UniversalConnectionPoolException e) {
            log.error("Error Creating UniversalConnectionPoolManager instance.", e);
        }
    }

    @Autowired
    @Qualifier("config_ds")
    public void setConfigDataSource(DataSource dataSource) {
        this.cds = dataSource;
    }

    @Bean
    public DataSource dataSource() {
        return multiTenantDataSource;
    }

    @Override
    public void afterPropertiesSet() {
        multiTenantDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return currentTenant.get();
            }
        };
        multiTenantDataSource.setTargetDataSources(tenantDataSources);
        multiTenantDataSource.setDefaultTargetDataSource(defaultDataSource());
        multiTenantDataSource.afterPropertiesSet();
    }

    @Override
    public void destroy() {
        for (Map.Entry<Object, Object> tenant : tenantDataSources.entrySet()) {
            this.removeTenant((String) tenant.getKey());
        }
    }

    public void setCurrentTenant(String tenantId) throws TenantNotFoundException {
        if (tenantIsAbsent(tenantId)) {
            try {
                Tenant tenant = getTenantById(tenantId);
                addTenant(tenant);
            } catch (SQLException e) {
                log.error("Error setting '{}' as current tenant.", tenantId, e);
            }
        }
        currentTenant.set(tenantId);
        log.debug("'{}' set as current tenant.", tenantId);
    }

    public Collection<Object> getTenantList() {
        return tenantDataSources.keySet();
    }

    public void removeTenant(String tenantId) {
        try {
            mgr.stopConnectionPool(tenantId);
            mgr.destroyConnectionPool(tenantId);
        } catch (UniversalConnectionPoolException e) {
            log.error("Error stopping connection pool.", e);
        }

        tenantDataSources.remove(tenantId);
        multiTenantDataSource.afterPropertiesSet();
        log.debug("Tenant '{}' removed", tenantId);
    }

    private DriverManagerDataSource defaultDataSource() {
        return new DriverManagerDataSource();
    }

    private boolean tenantIsAbsent(String tenantId) {
        return !tenantDataSources.containsKey(tenantId);
    }

    private Tenant getTenantById(String tenantId) throws TenantNotFoundException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(cds);
        List<Tenant> tenants = template
                .query("select * from tenants where tenant_id = :tenant_id",
                        new MapSqlParameterSource("tenant_id", tenantId),
                        new TenantRowMapper());
        if (tenants.isEmpty()) {
            log.warn("Tenant '{}' not found.", tenantId);
            throw new TenantNotFoundException(format("Tenant %s not found.", tenantId));
        }
        log.debug("Tenant found '{}'", tenantId);
        return tenants.get(0);
    }

    private void addTenant(Tenant tenant) throws SQLException {
        PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
        pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
        pds.setConnectionPoolName(tenant.getTenantId());
        pds.setUser(tenant.getDbUserName());
        pds.setPassword(Utils.comingIn(tenant.getDbPassword()));
        pds.setURL(tenant.getDbUrl());
        pds.setInitialPoolSize(tenant.getJdbcInitialLimit());
        pds.setMinPoolSize(tenant.getJdbcMinLimit());
        pds.setMaxPoolSize(tenant.getJdbcMaxLimit());
        pds.setMaxConnectionReuseCount(tenant.getJdbcMaxConnectionReuseCount());
        pds.setInactiveConnectionTimeout(tenant.getJdbcInactivityTimeout());
        pds.setAbandonedConnectionTimeout(tenant.getJdbcAbandonedConnectionTimeout());
        pds.setTimeoutCheckInterval(tenant.getJdbcTimeoutCheckInterval());
        pds.setMaxStatements(tenant.getJdbcMaxStatementsLimit());
        pds.setMaxConnectionReuseCount(tenant.getJdbcMaxConnectionReuseCount());

        // Check that new connection is 'live'. If not - throw exception
        try (Connection ignored = pds.getConnection()) {
            tenantDataSources.put(tenant.getTenantId(), pds);
            multiTenantDataSource.afterPropertiesSet();
            log.debug("Tenant '{}' added.", tenant.getTenantId());
        }
    }

    private static class TenantRowMapper implements RowMapper<Tenant> {
        @Override
        public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tenant tenant = new Tenant(
                    rs.getString("tenant_id"),
                    rs.getString("db_username"),
                    rs.getString("db_password"),
                    rs.getString("db_url"),
                    rs.getInt("jdbc_initial_limit"),
                    rs.getInt("jdbc_min_limit"),
                    rs.getInt("jdbc_max_limit"),
                    rs.getInt("jdbc_inactivity_timeout"),
                    rs.getInt("jdbc_abandoned_connection_timeout"),
                    rs.getInt("jdbc_timeout_Check_Interval"),
                    rs.getInt("jdbc_Max_Statements_Limit"),
                    rs.getInt("jdbc_Max_Connection_Reuse_Count")
            );
            tenant.setDescription(rs.getString("description"));
            return tenant;
        }
    }
}