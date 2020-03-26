package com.example.config.datasource;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class Tenant {
    private String tenantId;
    private String description;
    private String dbUserName;
    private String dbPassword;
    private String dbUrl;

    private int jdbcInitialLimit = 3;
    private int jdbcMinLimit = 2;
    private int jdbcMaxLimit = 10;
    private int jdbcInactivityTimeout = 1800;
    private int jdbcAbandonedConnectionTimeout = 900;
    private int jdbcTimeoutCheckInterval = 60;
    private int jdbcMaxStatementsLimit = 10;
    private int jdbcMaxConnectionReuseCount = 1000;

    protected Tenant() {
    }

    public Tenant(String tenantId, String dbUserName, String dbPassword, String dbUrl) {
        this.tenantId = tenantId;
        this.dbUserName = dbUserName;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
    }

    public Tenant(String tenantId, String dbUserName, String dbPassword, String dbUrl, int jdbcInitialLimit,
                  int jdbcMinLimit, int jdbcMaxLimit, int jdbcInactivityTimeout, int jdbcAbandonedConnectionTimeout,
                  int jdbcTimeoutCheckInterval, int jdbcMaxStatementsLimit, int jdbcMaxConnectionReuseCount) {
        this(tenantId, dbUserName, dbPassword, dbUrl);
        this.jdbcInitialLimit = jdbcInitialLimit;
        this.jdbcMinLimit = jdbcMinLimit;
        this.jdbcMaxLimit = jdbcMaxLimit;
        this.jdbcInactivityTimeout = jdbcInactivityTimeout;
        this.jdbcAbandonedConnectionTimeout = jdbcAbandonedConnectionTimeout;
        this.jdbcTimeoutCheckInterval = jdbcTimeoutCheckInterval;
        this.jdbcMaxStatementsLimit = jdbcMaxStatementsLimit;
        this.jdbcMaxConnectionReuseCount = jdbcMaxConnectionReuseCount;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public int getJdbcInitialLimit() {
        return jdbcInitialLimit;
    }

    public void setJdbcInitialLimit(int jdbcInitialLimit) {
        this.jdbcInitialLimit = jdbcInitialLimit;
    }

    public int getJdbcMinLimit() {
        return jdbcMinLimit;
    }

    public void setJdbcMinLimit(int jdbcMinLimit) {
        this.jdbcMinLimit = jdbcMinLimit;
    }

    public int getJdbcMaxLimit() {
        return jdbcMaxLimit;
    }

    public void setJdbcMaxLimit(int jdbcMaxLimit) {
        this.jdbcMaxLimit = jdbcMaxLimit;
    }

    public int getJdbcInactivityTimeout() {
        return jdbcInactivityTimeout;
    }

    public void setJdbcInactivityTimeout(int jdbcInactivityTimeout) {
        this.jdbcInactivityTimeout = jdbcInactivityTimeout;
    }

    public int getJdbcAbandonedConnectionTimeout() {
        return jdbcAbandonedConnectionTimeout;
    }

    public void setJdbcAbandonedConnectionTimeout(int jdbcAbandonedConnectionTimeout) {
        this.jdbcAbandonedConnectionTimeout = jdbcAbandonedConnectionTimeout;
    }

    public int getJdbcTimeoutCheckInterval() {
        return jdbcTimeoutCheckInterval;
    }

    public void setJdbcTimeoutCheckInterval(int jdbcTimeoutCheckInterval) {
        this.jdbcTimeoutCheckInterval = jdbcTimeoutCheckInterval;
    }

    public int getJdbcMaxStatementsLimit() {
        return jdbcMaxStatementsLimit;
    }

    public void setJdbcMaxStatementsLimit(int jdbcMaxStatementsLimit) {
        this.jdbcMaxStatementsLimit = jdbcMaxStatementsLimit;
    }

    public int getJdbcMaxConnectionReuseCount() {
        return jdbcMaxConnectionReuseCount;
    }

    public void setJdbcMaxConnectionReuseCount(int jdbcMaxConnectionReuseCount) {
        this.jdbcMaxConnectionReuseCount = jdbcMaxConnectionReuseCount;
    }
}
