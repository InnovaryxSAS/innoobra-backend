package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.ProjectStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateProjectRequestDTO {

    @JsonProperty("id")
    @NotBlank(message = "Project ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Project ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9]{1,255}$", message = "Project ID must contain only alphanumeric characters")
    private String id;

    @JsonProperty("name")
    @NotBlank(message = "Project name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Project name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @NotBlank(message = "Project description cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 500, message = "Project description must be between 1 and 500 characters")
    private String description;

    @JsonProperty("address")
    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @JsonProperty("city")
    @NotBlank(message = "City cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 50, message = "City must be between 1 and 50 characters")
    private String city;

    @JsonProperty("state")
    @NotBlank(message = "State cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "State must be between 1 and 100 characters")
    private String state;

    @JsonProperty("country")
    @NotBlank(message = "Country cannot be blank", groups = ValidationGroups.Create.class)
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "Country must be a 2 or 3 letter uppercase code")
    @Size(min = 2, max = 3, message = "Country code must be 2 or 3 characters")
    private String country;

    @JsonProperty("status")
    @ProjectStatusValid
    private String status = "active";

    @JsonProperty("responsibleUser")
    @NotBlank(message = "Responsible user cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Responsible user cannot exceed 255 characters")
    private String responsibleUser;

    @JsonProperty("dataSource")
    @NotBlank(message = "Data source cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Data source cannot exceed 255 characters")
    private String dataSource;

    @JsonProperty("company")
    @NotBlank(message = "Company cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Company cannot exceed 255 characters")
    private String company;

    @JsonProperty("createdBy")
    @NotBlank(message = "Created by cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Created by cannot exceed 255 characters")
    private String createdBy;

    @JsonProperty("budget")
    @NotNull(message = "Budget cannot be null", groups = ValidationGroups.Create.class)
    @DecimalMin(value = "0.00", message = "Budget must be greater than or equal to 0")
    @Digits(integer = 13, fraction = 2, message = "Budget must have at most 13 integer digits and 2 decimal places")
    private BigDecimal budget = BigDecimal.ZERO;

    @JsonProperty("inventory")
    @NotBlank(message = "Inventory cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 500, message = "Inventory must be between 1 and 500 characters")
    private String inventory;

    // Default constructor
    public CreateProjectRequestDTO() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(String responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public static class Builder {
        private final CreateProjectRequestDTO dto;

        public Builder() {
            dto = new CreateProjectRequestDTO();
        }

        public Builder id(String id) {
            dto.setId(id);
            return this;
        }

        public Builder name(String name) {
            dto.setName(name);
            return this;
        }

        public Builder description(String description) {
            dto.setDescription(description);
            return this;
        }

        public Builder address(String address) {
            dto.setAddress(address);
            return this;
        }

        public Builder city(String city) {
            dto.setCity(city);
            return this;
        }

        public Builder state(String state) {
            dto.setState(state);
            return this;
        }

        public Builder country(String country) {
            dto.setCountry(country);
            return this;
        }

        public Builder status(String status) {
            dto.setStatus(status);
            return this;
        }

        public Builder responsibleUser(String responsibleUser) {
            dto.setResponsibleUser(responsibleUser);
            return this;
        }

        public Builder dataSource(String dataSource) {
            dto.setDataSource(dataSource);
            return this;
        }

        public Builder company(String company) {
            dto.setCompany(company);
            return this;
        }

        public Builder createdBy(String createdBy) {
            dto.setCreatedBy(createdBy);
            return this;
        }

        public Builder budget(BigDecimal budget) {
            dto.setBudget(budget);
            return this;
        }

        public Builder inventory(String inventory) {
            dto.setInventory(inventory);
            return this;
        }

        public CreateProjectRequestDTO build() {
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "CreateProjectRequestDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", status='" + status + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}