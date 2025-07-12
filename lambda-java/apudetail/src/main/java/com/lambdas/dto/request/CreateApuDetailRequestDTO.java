package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.ApuDetailStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

public class CreateApuDetailRequestDTO {

    @JsonProperty("idApuDetail")
    @NotBlank(message = "APU Detail ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "APU Detail ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]{1,255}$", message = "APU Detail ID must contain only alphanumeric characters, underscores, and hyphens")
    private String idApuDetail;

    @JsonProperty("idActivity")
    @NotBlank(message = "Activity ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Activity ID cannot exceed 255 characters")
    private String idActivity;

    @JsonProperty("idAttribute")
    @NotBlank(message = "Attribute ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Attribute ID cannot exceed 255 characters")
    private String idAttribute;

    @JsonProperty("quantity")
    @NotNull(message = "Quantity cannot be null", groups = ValidationGroups.Create.class)
    @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be greater than or equal to 0")
    private Double quantity;

    @JsonProperty("wastePercentage")
    @DecimalMin(value = "0.0", inclusive = true, message = "Waste percentage must be greater than or equal to 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Waste percentage must be less than or equal to 100")
    private Double wastePercentage = 0.0;

    @JsonProperty("status")
    @ApuDetailStatusValid
    private String status = "active";

    // Default constructor
    public CreateApuDetailRequestDTO() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateApuDetailRequestDTO{" +
                "idApuDetail='" + idApuDetail + '\'' +
                ", idActivity='" + idActivity + '\'' +
                ", idAttribute='" + idAttribute + '\'' +
                ", quantity=" + quantity +
                ", wastePercentage=" + wastePercentage +
                ", status='" + status + '\'' +
                '}';
    }
}