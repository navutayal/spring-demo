package com.example.config.datasource;

import org.springframework.dao.DataAccessException;

public class TenantNotFoundException extends DataAccessException {
    public TenantNotFoundException(String message) {
        super(message);
    }
}
