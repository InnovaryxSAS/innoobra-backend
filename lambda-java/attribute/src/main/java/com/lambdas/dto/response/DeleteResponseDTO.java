package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("attributeId")
    private String attributeId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteResponseDTO() {
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String attributeId, boolean success) {
        this.message = message;
        this.attributeId = attributeId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String attributeId;
        private boolean success;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder attributeId(String attributeId) {
            this.attributeId = attributeId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public DeleteResponseDTO build() {
            return new DeleteResponseDTO(message, attributeId, success);
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

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "AttributeDeleteResponseDTO{" +
                "message='" + message + '\'' +
                ", attributeId='" + attributeId + '\'' +
                ", success=" + success +
                '}';
    }
}