package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lambdas.validation.annotations.StatusValid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProjectRequestDTO {

    @JsonProperty("name")
    @Size(min = 1, max = 100, message = "Project name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 500, message = "Project description cannot exceed 500 characters")
    private String description;

    @JsonProperty("address")
    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @JsonProperty("city")
    @Size(min = 1, max = 50, message = "City must be between 1 and 50 characters")
    private String city;

    @JsonProperty("state")
    @Size(min = 1, max = 50, message = "State must be between 1 and 50 characters")
    private String state;

    @JsonProperty("country")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be two uppercase letters")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    private String country;

    @JsonProperty("status")
    @StatusValid
    private String status;

    @JsonProperty("responsibleUser")
    private UUID responsibleUser;

    @JsonProperty("dataSourceId")
    private UUID dataSourceId;

    @JsonProperty("companyId")
    private UUID companyId;

    @JsonProperty("createdBy")
    private UUID createdBy;

    @JsonProperty("budgetAmount")
    @DecimalMin(value = "0.00", message = "Budget must be greater than or equal to 0")
    @Digits(integer = 12, fraction = 2, message = "Budget must have at most 12 integer digits and 2 decimal places")
    private BigDecimal budgetAmount;

    // Default constructor
    public UpdateProjectRequestDTO() {
    }

    // Getters and Setters
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

    public UUID getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(UUID responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public UUID getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(UUID dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public static class Builder {
        private final UpdateProjectRequestDTO dto;

        public Builder() {
            dto = new UpdateProjectRequestDTO();
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

        public Builder responsibleUser(UUID responsibleUser) {
            dto.setResponsibleUser(responsibleUser);
            return this;
        }

        public Builder dataSourceId(UUID dataSourceId) {
            dto.setDataSourceId(dataSourceId);
            return this;
        }

        public Builder companyId(UUID companyId) {
            dto.setCompanyId(companyId);
            return this;
        }

        public Builder createdBy(UUID createdBy) {
            dto.setCreatedBy(createdBy);
            return this;
        }

        public Builder budgetAmount(BigDecimal budgetAmount) {
            dto.setBudgetAmount(budgetAmount);
            return this;
        }

        public UpdateProjectRequestDTO build() {
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "UpdateProjectRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", status='" + status + '\'' +
                ", companyId=" + companyId +
                ", budgetAmount=" + budgetAmount +
                '}';
    }
}