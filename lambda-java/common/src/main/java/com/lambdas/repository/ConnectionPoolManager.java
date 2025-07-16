package com.lambdas.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private static ConnectionPoolManager instance;
    private HikariDataSource dataSource;
    private final Object lock = new Object();

    private ConnectionPoolManager() {
        initializePool();
    }

    public static ConnectionPoolManager getInstance() {
        if (instance == null) {
            synchronized (ConnectionPoolManager.class) {
                if (instance == null) {
                    instance = new ConnectionPoolManager();
                }
            }
        }
        return instance;
    }

    private void initializePool() {
        try {
            HikariConfig config = new HikariConfig();

            SecretsManagerUtil.DatabaseCredentials creds = SecretsManagerUtil.getCredentials();

            if (creds == null) {
                throw new RuntimeException("❌ No se pudieron obtener las credenciales de Secrets Manager");
            }

            config.setJdbcUrl(creds.getUrl());
            config.setUsername(creds.getUsername());
            config.setPassword(creds.getPassword());

            if (creds.getUrl().contains("localhost") || creds.getUrl().contains("127.0.0.1")) {
                throw new RuntimeException("❌ ERROR: Se está usando URL local! URL: " + creds.getUrl());
            }

            config.setMinimumIdle(2);
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(20000);
            config.setIdleTimeout(300000);
            config.setMaxLifetime(600000);
            config.setLeakDetectionThreshold(60000);
            config.setPoolName("CompanyManagementPool");
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(5000);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("reWriteBatchedInserts", "true");
            config.addDataSourceProperty("applicationName", "CompanyManagement");
            config.addDataSourceProperty("sslmode", "require");

            this.dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (lock) {
                if (dataSource == null || dataSource.isClosed()) {
                    initializePool();
                }
            }
        }
        
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (lock) {
                if (dataSource == null || dataSource.isClosed()) {
                    initializePool();
                }
            }
        }
        return dataSource;
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public String getPoolStats() {
        if (dataSource != null) {
            return String.format(
                    "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                    dataSource.getHikariPoolMXBean().getActiveConnections(),
                    dataSource.getHikariPoolMXBean().getIdleConnections(),
                    dataSource.getHikariPoolMXBean().getTotalConnections(),
                    dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
        return "Pool not initialized";
    }

    public boolean isHealthy() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                return false;
            }

            try (Connection conn = dataSource.getConnection()) {
                return conn.isValid(5);
            }
        } catch (SQLException e) {
            return false;
        }
    }
}