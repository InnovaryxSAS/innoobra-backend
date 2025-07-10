package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteResponseDTO() {
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String userId, boolean success) {
        this.message = message;
        this.userId = userId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String userId;
        private boolean success;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public DeleteResponseDTO build() {
            return new DeleteResponseDTO(message, userId, success);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "DeleteUserResponseDTO{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", success=" + success +
                '}';
    }
}