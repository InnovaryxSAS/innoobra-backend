package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class ApuDetail {

    @JsonProperty("idApuDetail")
    @NotBlank(message = "APU Detail ID cannot be blank")
    @Size(max = 255, message = "APU Detail ID cannot exceed 255 characters")
    private String idApuDetail;

    @JsonProperty("idActivity")
    @NotBlank(message = "Activity ID cannot be blank")
    @Size(max = 255, message = "Activity ID cannot exceed 255 characters")
    private String idActivity;

    @JsonProperty("idAttribute")
    @NotBlank(message = "Attribute ID cannot be blank")
    @Size(max = 255, message = "Attribute ID cannot exceed 255 characters")
    private String idAttribute;

    @JsonProperty("quantity")
    @NotNull(message = "Quantity cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be greater than or equal to 0")
    private Double quantity;

    @JsonProperty("wastePercentage")
    @NotNull(message = "Waste percentage cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Waste percentage must be greater than or equal to 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Waste percentage must be less than or equal to 100")
    private Double wastePercentage;

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
    private ApuDetailStatus status;

    // Default constructor
    public ApuDetail() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = ApuDetailStatus.ACTIVE;
        this.wastePercentage = 0.0;
    }

    // Constructor for existing APU details (when loading from database)
    public ApuDetail(String idApuDetail, String idActivity, String idAttribute, 
                    Double quantity, Double wastePercentage, LocalDateTime createdAt, 
                    LocalDateTime updatedAt, ApuDetailStatus status) {
        this.idApuDetail = idApuDetail;
        this.idActivity = idActivity;
        this.idAttribute = idAttribute;
        this.quantity = quantity;
        this.wastePercentage = wastePercentage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private String idApuDetail, idActivity, idAttribute;
        private Double quantity, wastePercentage;
        private LocalDateTime createdAt, updatedAt;
        private ApuDetailStatus status;
        private boolean isNewEntity = true;

        public Builder idApuDetail(String idApuDetail) {
            this.idApuDetail = idApuDetail;
            return this;
        }

        public Builder idActivity(String idActivity) {
            this.idActivity = idActivity;
            return this;
        }

        public Builder idAttribute(String idAttribute) {
            this.idAttribute = idAttribute;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder wastePercentage(Double wastePercentage) {
            this.wastePercentage = wastePercentage;
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

        public Builder status(ApuDetailStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public ApuDetail build() {
            ApuDetail apuDetail = new ApuDetail();
            apuDetail.idApuDetail = this.idApuDetail;
            apuDetail.idActivity = this.idActivity;
            apuDetail.idAttribute = this.idAttribute;
            apuDetail.quantity = this.quantity;
            apuDetail.wastePercentage = this.wastePercentage != null ? this.wastePercentage : 0.0;
            apuDetail.status = this.status != null ? this.status : ApuDetailStatus.ACTIVE;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                apuDetail.createdAt = now;
                apuDetail.updatedAt = now;
            } else {
                apuDetail.createdAt = this.createdAt != null ? this.createdAt : now;
                apuDetail.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return apuDetail;
        }
    }

    // Validation methods
    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            ApuDetailStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidQuantity(Double quantity) {
        return quantity != null && quantity >= 0;
    }

    public boolean isValidWastePercentage(Double wastePercentage) {
        return wastePercentage != null && wastePercentage >= 0 && wastePercentage <= 100;
    }

    public boolean hasRequiredFields() {
        return idApuDetail != null && !idApuDetail.trim().isEmpty() &&
               idActivity != null && !idActivity.trim().isEmpty() &&
               idAttribute != null && !idAttribute.trim().isEmpty() &&
               quantity != null && isValidQuantity(quantity) &&
               wastePercentage != null && isValidWastePercentage(wastePercentage);
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getIdApuDetail() {
        return idApuDetail;
    }

    public void setIdApuDetail(String idApuDetail) {
        if (!Objects.equals(this.idApuDetail, idApuDetail)) {
            this.idApuDetail = idApuDetail;
            updateTimestamp();
        }
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        if (!Objects.equals(this.idActivity, idActivity)) {
            this.idActivity = idActivity;
            updateTimestamp();
        }
    }

    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(String idAttribute) {
        if (!Objects.equals(this.idAttribute, idAttribute)) {
            this.idAttribute = idAttribute;
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

    public Double getWastePercentage() {
        return wastePercentage;
    }

    public void setWastePercentage(Double wastePercentage) {
        if (!Objects.equals(this.wastePercentage, wastePercentage)) {
            this.wastePercentage = wastePercentage;
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

    public ApuDetailStatus getStatus() {
        return status;
    }

    public void setStatus(ApuDetailStatus status) {
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
        ApuDetail apuDetail = (ApuDetail) o;
        return Objects.equals(idApuDetail, apuDetail.idApuDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idApuDetail);
    }

    @Override
    public String toString() {
        return "ApuDetail{" +
                "idApuDetail='" + idApuDetail + '\'' +
                ", idActivity='" + idActivity + '\'' +
                ", idAttribute='" + idAttribute + '\'' +
                ", quantity=" + quantity +
                ", wastePercentage=" + wastePercentage +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}