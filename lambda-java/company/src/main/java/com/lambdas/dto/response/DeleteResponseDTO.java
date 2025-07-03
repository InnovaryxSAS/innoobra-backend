package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("companyId")
    private String companyId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteResponseDTO() {
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String companyId, boolean success) {
        this.message = message;
        this.companyId = companyId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String companyId;
        private boolean success;

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

        public DeleteResponseDTO build() {
            return new DeleteResponseDTO(message, companyId, success);
        }
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

    @Override
    public String toString() {
        return "DeleteResponseDTO{" +
                "message='" + message + '\'' +
                ", companyId='" + companyId + '\'' +
                ", success=" + success +
                '}';
    }
}