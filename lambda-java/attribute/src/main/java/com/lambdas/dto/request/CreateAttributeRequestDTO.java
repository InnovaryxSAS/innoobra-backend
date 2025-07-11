package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.AttributeStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

public class CreateAttributeRequestDTO {

    @JsonProperty("idAttribute")
    @NotBlank(message = "Attribute ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Attribute ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]{1,255}$", message = "Attribute ID must contain only alphanumeric characters, underscores, and hyphens")
    private String idAttribute;

    @JsonProperty("idCompany")
    @NotBlank(message = "Company ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Company ID cannot exceed 255 characters")
    private String idCompany;

    @JsonProperty("code")
    @NotBlank(message = "Code cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Code must be between 1 and 100 characters")
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

    @JsonProperty("status")
    @AttributeStatusValid
    private String status = "active";

    // Default constructor
    public CreateAttributeRequestDTO() {
    }

    // Getters and Setters
    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateAttributeRequestDTO{" +
                "idAttribute='" + idAttribute + '\'' +
                ", idCompany='" + idCompany + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}