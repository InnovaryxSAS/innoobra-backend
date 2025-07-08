package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.CompanyStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

public class CreateCompanyRequestDTO {

    @JsonProperty("id")
    @NotBlank(message = "Company ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Company ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9]{1,255}$", message = "Company ID must contain only alphanumeric characters")
    private String id;

    @JsonProperty("name")
    @NotBlank(message = "Company name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Company name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("businessName")
    @NotBlank(message = "Business name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Business name must be between 1 and 100 characters")
    private String businessName;

    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank", groups = ValidationGroups.Create.class)
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

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
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be two uppercase letters")
    private String country;

    @JsonProperty("phoneNumber")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @JsonProperty("companyType")
    @Size(max = 100, message = "Company type cannot exceed 100 characters")
    private String companyType;

    @JsonProperty("address")
    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @JsonProperty("legalRepresentative")
    @Size(max = 100, message = "Legal representative cannot exceed 100 characters")
    private String legalRepresentative;

    @JsonProperty("status")
    @CompanyStatusValid
    private String status = "active"; 

    // Default constructor
    public CreateCompanyRequestDTO() {
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
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

    @Override
    public String toString() {
        return "CreateCompanyRequestDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", businessName='" + businessName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}