package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Company {

    @JsonProperty("id")
    @NotBlank(message = "Company ID cannot be blank")
    @Size(max = 255, message = "Company ID cannot exceed 255 characters")
    private String id;

    @JsonProperty("name")
    @NotBlank(message = "Company name cannot be blank")
    @Size(min = 1, max = 100, message = "Company name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("businessName")
    @NotBlank(message = "Business name cannot be blank")
    @Size(min = 1, max = 100, message = "Business name must be between 1 and 100 characters")
    private String businessName;

    @JsonProperty("companyType")
    @Size(max = 100, message = "Company type cannot exceed 100 characters")
    private String companyType;

    @JsonProperty("address")
    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @JsonProperty("phoneNumber")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in international format (e.g., +1234567890)")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @JsonProperty("legalRepresentative")
    @Size(max = 100, message = "Legal representative cannot exceed 100 characters")
    private String legalRepresentative;

    @JsonProperty("city")
    @NotBlank(message = "City cannot be blank")
    @Size(min = 1, max = 50, message = "City must be between 1 and 50 characters")
    private String city;

    @JsonProperty("state")
    @NotBlank(message = "State cannot be blank")
    @Size(min = 1, max = 100, message = "State must be between 1 and 100 characters")
    private String state;

    @JsonProperty("country")
    @NotBlank(message = "Country cannot be blank")
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "Country must be a 2 or 3 letter uppercase code")
    @Size(min = 2, max = 3, message = "Country code must be 2 or 3 characters")
    private String country;

    @JsonProperty("createdAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Updated at cannot be null")
    private LocalDateTime updatedAt;

    @JsonProperty("status")
    @NotNull(message = "Status cannot be null")
    private CompanyStatus status;

    // Default constructor
    public Company() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = CompanyStatus.ACTIVE;
    }

    // Constructor for existing companies (when loading from database)
    public Company(String id, String name, String businessName, String companyType, 
                   String address, String phoneNumber, String email, String legalRepresentative,
                   String city, String state, String country, LocalDateTime createdAt, 
                   LocalDateTime updatedAt, CompanyStatus status) {
        this.id = id;
        this.name = name;
        this.businessName = businessName;
        this.companyType = companyType;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.legalRepresentative = legalRepresentative;
        this.city = city;
        this.state = state;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private String id, name, businessName, companyType, address, phoneNumber, email, 
                legalRepresentative, city, state, country;
        private LocalDateTime createdAt, updatedAt;
        private CompanyStatus status;
        private boolean isNewEntity = true;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public Builder companyType(String companyType) {
            this.companyType = companyType;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder legalRepresentative(String legalRepresentative) {
            this.legalRepresentative = legalRepresentative;
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
            this.isNewEntity = false;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder status(CompanyStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Company build() {
            Company company = new Company();
            company.id = this.id;
            company.name = this.name;
            company.businessName = this.businessName;
            company.companyType = this.companyType;
            company.address = this.address;
            company.phoneNumber = this.phoneNumber;
            company.email = this.email;
            company.legalRepresentative = this.legalRepresentative;
            company.city = this.city;
            company.state = this.state;
            company.country = this.country;
            company.status = this.status != null ? this.status : CompanyStatus.ACTIVE;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                company.createdAt = now;
                company.updatedAt = now;
            } else {
                company.createdAt = this.createdAt != null ? this.createdAt : now;
                company.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return company;
        }
    }

    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            CompanyStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return true;
        return phoneNumber.matches("^\\+[1-9]\\d{1,14}$");
    }

    public boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) return false;
        return countryCode.matches("^[A-Z]{2,3}$");
    }

    public boolean hasRequiredFields() {
        return id != null && !id.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               businessName != null && !businessName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               state != null && !state.trim().isEmpty() &&
               country != null && !country.trim().isEmpty();
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        if (!Objects.equals(this.businessName, businessName)) {
            this.businessName = businessName;
            updateTimestamp();
        }
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        if (!Objects.equals(this.companyType, companyType)) {
            this.companyType = companyType;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (!Objects.equals(this.phoneNumber, phoneNumber)) {
            this.phoneNumber = phoneNumber;
            updateTimestamp();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!Objects.equals(this.email, email)) {
            this.email = email;
            updateTimestamp();
        }
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        if (!Objects.equals(this.legalRepresentative, legalRepresentative)) {
            this.legalRepresentative = legalRepresentative;
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

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
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
        Company company = (Company) o;
        return Objects.equals(id, company.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", businessName='" + businessName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}