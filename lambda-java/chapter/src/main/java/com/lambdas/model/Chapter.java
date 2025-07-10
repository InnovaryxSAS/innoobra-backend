package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Chapter {

    @JsonProperty("idChapter")
    @NotBlank(message = "Chapter ID cannot be blank")
    @Size(max = 255, message = "Chapter ID cannot exceed 255 characters")
    private String idChapter;

    @JsonProperty("idBudget")
    @NotBlank(message = "Budget ID cannot be blank")
    @Size(max = 255, message = "Budget ID cannot exceed 255 characters")
    private String idBudget;

    @JsonProperty("code")
    @NotBlank(message = "Code cannot be blank")
    @Size(min = 1, max = 100, message = "Code must be between 1 and 100 characters")
    private String code;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @JsonProperty("createdAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Updated at cannot be null")
    private LocalDateTime updatedAt;

    @JsonProperty("status")
    @NotNull(message = "Status cannot be null")
    private ChapterStatus status;

    // Default constructor
    public Chapter() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = ChapterStatus.ACTIVE;
    }

    // Constructor for existing chapters (when loading from database)
    public Chapter(String idChapter, String idBudget, String code, String name, String description,
                   LocalDateTime createdAt, LocalDateTime updatedAt, ChapterStatus status) {
        this.idChapter = idChapter;
        this.idBudget = idBudget;
        this.code = code;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private String idChapter, idBudget, code, name, description;
        private LocalDateTime createdAt, updatedAt;
        private ChapterStatus status;
        private boolean isNewEntity = true;

        public Builder idChapter(String idChapter) {
            this.idChapter = idChapter;
            return this;
        }

        public Builder idBudget(String idBudget) {
            this.idBudget = idBudget;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            this.isNewEntity = false;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder status(ChapterStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Chapter build() {
            Chapter chapter = new Chapter();
            chapter.idChapter = this.idChapter;
            chapter.idBudget = this.idBudget;
            chapter.code = this.code;
            chapter.name = this.name;
            chapter.description = this.description;
            chapter.status = this.status != null ? this.status : ChapterStatus.ACTIVE;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                chapter.createdAt = now;
                chapter.updatedAt = now;
            } else {
                chapter.createdAt = this.createdAt != null ? this.createdAt : now;
                chapter.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return chapter;
        }
    }

    // Validation methods
    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            ChapterStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidCode(String code) {
        if (code == null || code.trim().isEmpty()) return false;
        return code.trim().length() >= 1 && code.length() <= 100;
    }

    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return name.trim().length() >= 1 && name.trim().length() <= 100;
    }

    public boolean isValidDescription(String description) {
        return description == null || description.length() <= 200;
    }

    public boolean hasRequiredFields() {
        return idChapter != null && !idChapter.trim().isEmpty() &&
               idBudget != null && !idBudget.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() &&
               name != null && !name.trim().isEmpty();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getIdChapter() {
        return idChapter;
    }

    public void setIdChapter(String idChapter) {
        if (!Objects.equals(this.idChapter, idChapter)) {
            this.idChapter = idChapter;
            updateTimestamp();
        }
    }

    public String getIdBudget() {
        return idBudget;
    }

    public void setIdBudget(String idBudget) {
        if (!Objects.equals(this.idBudget, idBudget)) {
            this.idBudget = idBudget;
            updateTimestamp();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (!Objects.equals(this.code, code)) {
            this.code = code;
            updateTimestamp();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!Objects.equals(this.name, name)) {
            this.name = name;
            updateTimestamp();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!Objects.equals(this.description, description)) {
            this.description = description;
            updateTimestamp();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (this.createdAt == null) {
            this.createdAt = createdAt;
        }
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ChapterStatus getStatus() {
        return status;
    }

    public void setStatus(ChapterStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
            updateTimestamp();
        }
    }

    public void touch() {
        updateTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(idChapter, chapter.idChapter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idChapter);
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "idChapter='" + idChapter + '\'' +
                ", idBudget='" + idBudget + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}