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

    //private static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/inno_obra";
    private static final String LOCAL_DB_URL = "jdbc:postgresql://host.docker.internal:5432/inno_obra";
    private static final String LOCAL_DB_USERNAME = "postgres";
    private static final String LOCAL_DB_PASSWORD = "2616";
    private static final String LOCAL_DB_SSL_MODE = "disable";

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

            String dbUrl = getConfigValue("DB_URL", LOCAL_DB_URL);
            String dbUsername = getConfigValue("DB_USERNAME", LOCAL_DB_USERNAME);
            String dbPassword = getConfigValue("DB_PASSWORD", LOCAL_DB_PASSWORD);
            String sslMode = getConfigValue("DB_SSL_MODE", LOCAL_DB_SSL_MODE);

            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);

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

            config.addDataSourceProperty("sslmode", sslMode);

            this.dataSource = new HikariDataSource(config);

            System.out.println("Database connection initialized:");
            System.out.println("URL: " + dbUrl);
            System.out.println("Username: " + dbUsername);
            System.out.println("SSL Mode: " + sslMode);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    private String getConfigValue(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(key);
        }
        if (value == null || value.trim().isEmpty()) {
            value = defaultValue;
        }
        return value;
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