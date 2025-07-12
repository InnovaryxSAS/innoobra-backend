package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class ApuDetailResponseDTO {

    @JsonProperty("idApuDetail")
    private String idApuDetail;

    @JsonProperty("idActivity")
    private String idActivity;

    @JsonProperty("idAttribute")
    private String idAttribute;

    @JsonProperty("quantity")
    private Double quantity;

    @JsonProperty("wastePercentage")
    private Double wastePercentage;

    @JsonProperty("createdAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @JsonProperty("status")
    private String status;

    // Default constructor
    public ApuDetailResponseDTO() {
    }

    // Builder pattern
    public static class Builder {
        private String idApuDetail, idActivity, idAttribute, status;
        private Double quantity, wastePercentage;
        private LocalDateTime createdAt, updatedAt;

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
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public ApuDetailResponseDTO build() {
            ApuDetailResponseDTO dto = new ApuDetailResponseDTO();
            dto.idApuDetail = this.idApuDetail;
            dto.idActivity = this.idActivity;
            dto.idAttribute = this.idAttribute;
            dto.quantity = this.quantity;
            dto.wastePercentage = this.wastePercentage;
            dto.createdAt = this.createdAt;
            dto.updatedAt = this.updatedAt;
            dto.status = this.status;
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getIdApuDetail() {
        return idApuDetail;
    }

    public void setIdApuDetail(String idApuDetail) {
        this.idApuDetail = idApuDetail;
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getWastePercentage() {
        return wastePercentage;
    }

    public void setWastePercentage(Double wastePercentage) {
        this.wastePercentage = wastePercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ApuDetailResponseDTO{" +
                "idApuDetail='" + idApuDetail + '\'' +
                ", idActivity='" + idActivity + '\'' +
                ", idAttribute='" + idAttribute + '\'' +
                ", quantity=" + quantity +
                ", wastePercentage=" + wastePercentage +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}