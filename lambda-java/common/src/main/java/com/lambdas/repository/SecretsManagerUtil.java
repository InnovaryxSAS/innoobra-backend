package com.lambdas.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretsManagerUtil {

    private static final String SECRET_NAME = "innobra-dev-db-credentials";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static DatabaseCredentials getCredentials() {
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(SECRET_NAME)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            String secretString = response.secretString();

            if (secretString == null || secretString.trim().isEmpty()) {
                throw new RuntimeException("❌ Secret string is empty or null");
            }

            JsonNode secretJson = objectMapper.readTree(secretString);

            if (!secretJson.has("username") || !secretJson.has("password")) {
                throw new RuntimeException("❌ Secret JSON missing required fields (username/password)");
            }

            String dbHost = ParameterStoreUtil.getDbHost();
            
            if (dbHost == null || dbHost.trim().isEmpty()) {
                throw new RuntimeException("❌ DB host is empty or null");
            }

            String dbName = "innobra_dev";
            String jdbcUrl = "jdbc:postgresql://" + dbHost + ":5432/" + dbName;

            if (jdbcUrl.contains("localhost") || jdbcUrl.contains("127.0.0.1")) {
                throw new RuntimeException("❌ CRITICAL ERROR: Generated URL contains localhost! URL: " + jdbcUrl);
            }

            String username = secretJson.get("username").asText();
            String password = secretJson.get("password").asText();

            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("❌ Username is empty or null");
            }

            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("❌ Password is empty or null");
            }

            return new DatabaseCredentials(username, password, jdbcUrl);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving database credentials from Secrets Manager", e);
        }
    }

    public static class DatabaseCredentials {
        private final String username;
        private final String password;
        private final String url;

        public DatabaseCredentials(String username, String password, String url) {
            this.username = username;
            this.password = password;
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getUrl() {
            return url;
        }
    }
}