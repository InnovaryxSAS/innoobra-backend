package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.ApuDetailStatusValid;
import jakarta.validation.constraints.*;

public class UpdateApuDetailRequestDTO {

    @JsonProperty("quantity")
    @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be greater than or equal to 0")
    private Double quantity;

    @JsonProperty("wastePercentage")
    @DecimalMin(value = "0.0", inclusive = true, message = "Waste percentage must be greater than or equal to 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Waste percentage must be less than or equal to 100")
    private Double wastePercentage;

    @JsonProperty("status")
    @ApuDetailStatusValid
    private String status;

    // Default constructor
    public UpdateApuDetailRequestDTO() {
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateApuDetailRequestDTO{" +
                "quantity=" + quantity +
                ", wastePercentage=" + wastePercentage +
                ", status='" + status + '\'' +
                '}';
    }
}