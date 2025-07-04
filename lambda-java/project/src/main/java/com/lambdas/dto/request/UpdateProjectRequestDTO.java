package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class UpdateProjectRequestDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("status")
    private String status;

    @JsonProperty("responsibleUser")
    private String responsibleUser;

    @JsonProperty("dataSource")
    private String dataSource;

    @JsonProperty("company")
    private String company;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("budget")
    private BigDecimal budget;

    @JsonProperty("inventory")
    private String inventory;

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

        public UpdateProjectRequestDTO build() {
            return dto;
        }
    }

    // Método de acceso para iniciar el builder
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "UpdateProjectRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", status='" + status + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}