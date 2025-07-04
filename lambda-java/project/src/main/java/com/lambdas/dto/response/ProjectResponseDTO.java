package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProjectResponseDTO {

    @JsonProperty("id")
    private String id;

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
    public ProjectResponseDTO() {
    }

    // Builder pattern
    public static class Builder {
        private String id, name, description, address, city, state, country;
        private String status, responsibleUser, dataSource, company, createdBy, inventory;
        private LocalDateTime createdAt, updatedAt;
        private BigDecimal budget;

        public Builder id(String id) {
            this.id = id;
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

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
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

        public Builder responsibleUser(String responsibleUser) {
            this.responsibleUser = responsibleUser;
            return this;
        }

        public Builder dataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder budget(BigDecimal budget) {
            this.budget = budget;
            return this;
        }

        public Builder inventory(String inventory) {
            this.inventory = inventory;
            return this;
        }

        public ProjectResponseDTO build() {
            ProjectResponseDTO dto = new ProjectResponseDTO();
            dto.id = this.id;
            dto.name = this.name;
            dto.description = this.description;
            dto.address = this.address;
            dto.city = this.city;
            dto.state = this.state;
            dto.country = this.country;
            dto.createdAt = this.createdAt;
            dto.updatedAt = this.updatedAt;
            dto.status = this.status;
            dto.responsibleUser = this.responsibleUser;
            dto.dataSource = this.dataSource;
            dto.company = this.company;
            dto.createdBy = this.createdBy;
            dto.budget = this.budget;
            dto.inventory = this.inventory;
            return dto;
        }
    }

    public static Builder builder() {
    return new Builder();
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

    @Override
    public String toString() {
        return "ProjectResponseDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", status='" + status + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}