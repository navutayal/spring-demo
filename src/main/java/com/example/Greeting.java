package com.example;

import com.example.config.datasource.MultiTenantManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.List;

@RestController
public class Greeting {

    private static final Logger log = LogManager.getLogger(Greeting.class);

    private MultiTenantManager tenantManager;
    private DataSource dataSource;

    @Autowired
    public Greeting(MultiTenantManager tenantManager, DataSource dataSource) {
        this.tenantManager = tenantManager;
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> hello(@RequestHeader(value = "X-TenantID") String tenantId) {
        tenantManager.setCurrentTenant(tenantId);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return ResponseEntity
                .ok()
                .body(template
                        .query("select username from user_users", (rs, rowNum) -> rs.getString("username"))
                );
    }

    @GetMapping("/dba")
    String dba() {
        return "DBA";
    }

    @GetMapping("/admin")
    String admin() {
        return "ADMIN";
    }

    @GetMapping("/update")
    String update() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("com.example");
        loggerConfig.setLevel(Level.INFO);
        ctx.updateLoggers();
        return "Updated";
    }

    @GetMapping("/revert")
    String revert() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("com.example");
        loggerConfig.setLevel(Level.DEBUG);
        ctx.updateLoggers();
        return "Updated";
    }

}