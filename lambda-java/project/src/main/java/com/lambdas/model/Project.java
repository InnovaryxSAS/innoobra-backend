package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Project {

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
    private ProjectStatus status;

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
    public Project() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = ProjectStatus.ACTIVE;
        this.budget = BigDecimal.ZERO;
    }

    // Constructor for existing projects (when loading from database)
    public Project(String id, String name, String description, String address, String city, 
                   String state, String country, LocalDateTime createdAt, LocalDateTime updatedAt,
                   ProjectStatus status, String responsibleUser, String dataSource, String company,
                   String createdBy, BigDecimal budget, String inventory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.responsibleUser = responsibleUser;
        this.dataSource = dataSource;
        this.company = company;
        this.createdBy = createdBy;
        this.budget = budget;
        this.inventory = inventory;
    }

    // Builder pattern
    public static class Builder {
        private String id, name, description, address, city, state, country;
        private String responsibleUser, dataSource, company, createdBy, inventory;
        private LocalDateTime createdAt, updatedAt;
        private ProjectStatus status;
        private BigDecimal budget;
        private boolean isNewEntity = true;

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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            this.isNewEntity = false;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder status(ProjectStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.id = this.id;
            project.name = this.name;
            project.description = this.description;
            project.address = this.address;
            project.city = this.city;
            project.state = this.state;
            project.country = this.country;
            project.responsibleUser = this.responsibleUser;
            project.dataSource = this.dataSource;
            project.company = this.company;
            project.createdBy = this.createdBy;
            project.inventory = this.inventory;
            project.status = this.status != null ? this.status : ProjectStatus.ACTIVE;
            project.budget = this.budget != null ? this.budget : BigDecimal.ZERO;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                project.createdAt = now;
                project.updatedAt = now;
            } else {
                project.createdAt = this.createdAt != null ? this.createdAt : now;
                project.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return project;
        }
    }

    public static Builder builder() {
    return new Builder();
}


    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (!Objects.equals(this.id, id)) {
            this.id = id;
            updateTimestamp();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!Objects.equals(this.name, name)) {
            this.name = name;
            updateTimestamp();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!Objects.equals(this.description, description)) {
            this.description = description;
            updateTimestamp();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (!Objects.equals(this.address, address)) {
            this.address = address;
            updateTimestamp();
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (!Objects.equals(this.city, city)) {
            this.city = city;
            updateTimestamp();
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (!Objects.equals(this.state, state)) {
            this.state = state;
            updateTimestamp();
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (!Objects.equals(this.country, country)) {
            this.country = country;
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
            updateTimestamp();
        }
    }

    public String getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(String responsibleUser) {
        if (!Objects.equals(this.responsibleUser, responsibleUser)) {
            this.responsibleUser = responsibleUser;
            updateTimestamp();
        }
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        if (!Objects.equals(this.dataSource, dataSource)) {
            this.dataSource = dataSource;
            updateTimestamp();
        }
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        if (!Objects.equals(this.company, company)) {
            this.company = company;
            updateTimestamp();
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        if (!Objects.equals(this.createdBy, createdBy)) {
            this.createdBy = createdBy;
            updateTimestamp();
        }
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        if (!Objects.equals(this.budget, budget)) {
            this.budget = budget;
            updateTimestamp();
        }
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        if (!Objects.equals(this.inventory, inventory)) {
            this.inventory = inventory;
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
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", status=" + status +
                ", responsibleUser='" + responsibleUser + '\'' +
                ", company='" + company + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", budget=" + budget +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}