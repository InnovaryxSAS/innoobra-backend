package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.ActivityStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

public class CreateActivityRequestDTO {

    @JsonProperty("idActivity")
    @NotBlank(message = "Activity ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Activity ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]{1,255}$", message = "Activity ID must contain only alphanumeric characters, underscores, and hyphens")
    private String idActivity;

    @JsonProperty("idChapter")
    @NotBlank(message = "Chapter ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Chapter ID cannot exceed 255 characters")
    private String idChapter;

    @JsonProperty("code")
    @NotBlank(message = "Code cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 255, message = "Code must be between 1 and 255 characters")
    private String code;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @JsonProperty("unit")
    @NotBlank(message = "Unit cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 50, message = "Unit must be between 1 and 50 characters")
    private String unit;

    @JsonProperty("quantity")
    @NotNull(message = "Quantity cannot be null", groups = ValidationGroups.Create.class)
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Double quantity;

    @JsonProperty("status")
    @ActivityStatusValid
    private String status = "active";

    // Default constructor
    public CreateActivityRequestDTO() {
    }

    // Getters and Setters
    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

    public String getIdChapter() {
        return idChapter;
    }

    public void setIdChapter(String idChapter) {
        this.idChapter = idChapter;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateActivityRequestDTO{" +
                "idActivity='" + idActivity + '\'' +
                ", idChapter='" + idChapter + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }
}