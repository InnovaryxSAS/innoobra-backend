package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponseDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("chapterId")
    private String chapterId;

    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public DeleteResponseDTO() {
    }

    // Constructor with parameters
    public DeleteResponseDTO(String message, String chapterId, boolean success) {
        this.message = message;
        this.chapterId = chapterId;
        this.success = success;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private String chapterId;
        private boolean success;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder chapterId(String chapterId) {
            this.chapterId = chapterId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public DeleteResponseDTO build() {
            return new DeleteResponseDTO(message, chapterId, success);
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

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "DeleteChapterResponseDTO{" +
                "message='" + message + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", success=" + success +
                '}';
    }
}