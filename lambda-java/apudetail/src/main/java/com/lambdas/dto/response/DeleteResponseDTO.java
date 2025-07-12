package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("apuDetailId")
    private String apuDetailId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteResponseDTO() {
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String apuDetailId, boolean success) {
        this.message = message;
        this.apuDetailId = apuDetailId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String apuDetailId;
        private boolean success;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder apuDetailId(String apuDetailId) {
            this.apuDetailId = apuDetailId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public DeleteResponseDTO build() {
            return new DeleteResponseDTO(message, apuDetailId, success);
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

    public String getApuDetailId() {
        return apuDetailId;
    }

    public void setApuDetailId(String apuDetailId) {
        this.apuDetailId = apuDetailId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ApuDetailDeleteResponseDTO{" +
                "message='" + message + '\'' +
                ", apuDetailId='" + apuDetailId + '\'' +
                ", success=" + success +
                '}';
    }
}