package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class DeleteRoleResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("roleId")
    private String roleId;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("timestamp")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;

    // Default constructor
    public DeleteRoleResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with parameters
    public DeleteRoleResponseDTO(String message, String roleId, boolean success) {
        this.message = message;
        this.roleId = roleId;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    // Full constructor
    public DeleteRoleResponseDTO(String message, String roleId, boolean success, LocalDateTime timestamp) {
        this.message = message;
        this.roleId = roleId;
        this.success = success;
        this.timestamp = timestamp;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String roleId;
        private boolean success;
        private LocalDateTime timestamp;

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

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public DeleteRoleResponseDTO build() {
            DeleteRoleResponseDTO dto = new DeleteRoleResponseDTO();
            dto.message = this.message;
            dto.roleId = this.roleId;
            dto.success = this.success;
            dto.timestamp = this.timestamp != null ? this.timestamp : LocalDateTime.now();
            return dto;
        }
    }

    // Factory methods for common responses
    public static DeleteRoleResponseDTO success(String roleId, String message) {
        return new Builder()
                .roleId(roleId)
                .message(message)
                .success(true)
                .build();
    }

    public static DeleteRoleResponseDTO error(String roleId, String message) {
        return new Builder()
                .roleId(roleId)
                .message(message)
                .success(false)
                .build();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DeleteRoleResponseDTO{" +
                "message='" + message + '\'' +
                ", roleId='" + roleId + '\'' +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}