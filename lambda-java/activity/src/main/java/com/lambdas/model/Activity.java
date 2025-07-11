package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Activity {

    @JsonProperty("idActivity")
    @NotBlank(message = "Activity ID cannot be blank")
    @Size(max = 255, message = "Activity ID cannot exceed 255 characters")
    private String idActivity;

    @JsonProperty("idChapter")
    @NotBlank(message = "Chapter ID cannot be blank")
    @Size(max = 255, message = "Chapter ID cannot exceed 255 characters")
    private String idChapter;

    @JsonProperty("code")
    @NotBlank(message = "Code cannot be blank")
    @Size(min = 1, max = 255, message = "Code must be between 1 and 255 characters")
    private String code;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @JsonProperty("unit")
    @NotBlank(message = "Unit cannot be blank")
    @Size(min = 1, max = 50, message = "Unit must be between 1 and 50 characters")
    private String unit;

    @JsonProperty("quantity")
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Double quantity;

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
    private ActivityStatus status;

    // Default constructor
    public Activity() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = ActivityStatus.ACTIVE;
    }

    // Constructor for existing activities (when loading from database)
    public Activity(String idActivity, String idChapter, String code, String name, String description,
                    String unit, Double quantity, LocalDateTime createdAt, LocalDateTime updatedAt,
                    ActivityStatus status) {
        this.idActivity = idActivity;
        this.idChapter = idChapter;
        this.code = code;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private String idActivity, idChapter, code, name, description, unit;
        private Double quantity;
        private LocalDateTime createdAt, updatedAt;
        private ActivityStatus status;
        private boolean isNewEntity = true;

        public Builder idActivity(String idActivity) {
            this.idActivity = idActivity;
            return this;
        }

        public Builder idChapter(String idChapter) {
            this.idChapter = idChapter;
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

        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
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

        public Builder status(ActivityStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Activity build() {
            Activity activity = new Activity();
            activity.idActivity = this.idActivity;
            activity.idChapter = this.idChapter;
            activity.code = this.code;
            activity.name = this.name;
            activity.description = this.description;
            activity.unit = this.unit;
            activity.quantity = this.quantity;
            activity.status = this.status != null ? this.status : ActivityStatus.ACTIVE;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                activity.createdAt = now;
                activity.updatedAt = now;
            } else {
                activity.createdAt = this.createdAt != null ? this.createdAt : now;
                activity.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return activity;
        }
    }

    // Validation methods
    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            ActivityStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidQuantity(Double quantity) {
        return quantity != null && quantity >= 0;
    }

    public boolean hasRequiredFields() {
        return idActivity != null && !idActivity.trim().isEmpty() &&
               idChapter != null && !idChapter.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               unit != null && !unit.trim().isEmpty() &&
               quantity != null && quantity >= 0;
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        if (!Objects.equals(this.idActivity, idActivity)) {
            this.idActivity = idActivity;
            updateTimestamp();
        }
    }

    public String getIdChapter() {
        return idChapter;
    }

    public void setIdChapter(String idChapter) {
        if (!Objects.equals(this.idChapter, idChapter)) {
            this.idChapter = idChapter;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        if (!Objects.equals(this.unit, unit)) {
            this.unit = unit;
            updateTimestamp();
        }
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        if (!Objects.equals(this.quantity, quantity)) {
            this.quantity = quantity;
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

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
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
        Activity activity = (Activity) o;
        return Objects.equals(idActivity, activity.idActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idActivity);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "idActivity='" + idActivity + '\'' +
                ", idChapter='" + idChapter + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}