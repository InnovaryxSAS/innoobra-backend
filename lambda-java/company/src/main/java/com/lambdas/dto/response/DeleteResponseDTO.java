package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("companyId")
    private String companyId;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("timestamp")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;

    // Default constructor
    public DeleteResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String companyId, boolean success) {
        this.message = message;
        this.companyId = companyId;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    // Full constructor
    public DeleteResponseDTO(String message, String companyId, boolean success, LocalDateTime timestamp) {
        this.message = message;
        this.companyId = companyId;
        this.success = success;
        this.timestamp = timestamp;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String companyId;
        private boolean success;
        private LocalDateTime timestamp;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder companyId(String companyId) {
            this.companyId = companyId;
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

        public DeleteResponseDTO build() {
            DeleteResponseDTO dto = new DeleteResponseDTO();
            dto.message = this.message;
            dto.companyId = this.companyId;
            dto.success = this.success;
            dto.timestamp = this.timestamp != null ? this.timestamp : LocalDateTime.now();
            return dto;
        }
    }

    // Factory methods for common responses
    public static DeleteResponseDTO success(String companyId, String message) {
        return new Builder()
                .companyId(companyId)
                .message(message)
                .success(true)
                .build();
    }

    public static DeleteResponseDTO error(String companyId, String message) {
        return new Builder()
                .companyId(companyId)
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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
        return "DeleteResponseDTO{" +
                "message='" + message + '\'' +
                ", companyId='" + companyId + '\'' +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}