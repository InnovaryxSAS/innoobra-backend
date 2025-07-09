package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteRoleResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("roleId")
    private String roleId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteRoleResponseDTO() {
    }

    // Constructor with parameters
    public DeleteRoleResponseDTO(String message, String roleId, boolean success) {
        this.message = message;
        this.roleId = roleId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String roleId;
        private boolean success;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder roleId(String roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public DeleteRoleResponseDTO build() {
            return new DeleteRoleResponseDTO(message, roleId, success);
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "DeleteRoleResponseDTO{" +
                "message='" + message + '\'' +
                ", roleId='" + roleId + '\'' +
                ", success=" + success +
                '}';
    }
}