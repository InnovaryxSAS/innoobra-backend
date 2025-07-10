package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("activityId")
    private String activityId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteResponseDTO() {
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String activityId, boolean success) {
        this.message = message;
        this.activityId = activityId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String activityId;
        private boolean success;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder activityId(String activityId) {
            this.activityId = activityId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public DeleteResponseDTO build() {
            return new DeleteResponseDTO(message, activityId, success);
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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "DeleteActivityResponseDTO{" +
                "message='" + message + '\'' +
                ", activityId='" + activityId + '\'' +
                ", success=" + success +
                '}';
    }
}