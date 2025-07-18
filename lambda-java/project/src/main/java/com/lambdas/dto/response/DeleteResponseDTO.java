package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteResponseDTO() {
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String projectId, boolean success) {
        this.message = message;
        this.projectId = projectId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String projectId;
        private boolean success;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public DeleteResponseDTO build() {
            return new DeleteResponseDTO(message, projectId, success);
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "DeleteProjectResponseDTO{" +
                "message='" + message + '\'' +
                ", projectId='" + projectId + '\'' +
                ", success=" + success +
                '}';
    }
}