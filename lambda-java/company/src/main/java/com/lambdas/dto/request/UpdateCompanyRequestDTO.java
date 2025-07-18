package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.StatusValid;

import jakarta.validation.constraints.*;
import java.util.UUID;

public class UpdateCompanyRequestDTO {

    @JsonProperty("taxId")
    private UUID taxId;

    @JsonProperty("nit")
    @Size(max = 20, message = "NIT cannot exceed 20 characters")
    private String nit;

    @JsonProperty("name")
    @Size(min = 1, max = 100, message = "Company name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("businessName")
    @Size(min = 1, max = 100, message = "Business name must be between 1 and 100 characters")
    private String businessName;

    @JsonProperty("companyType")
    @Size(max = 50, message = "Company type cannot exceed 50 characters")
    private String companyType;

    @JsonProperty("address")
    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @JsonProperty("phoneNumber")
    @Pattern(regexp = "^\\+\\d{1,15}$", message = "Phone number must be in international format (+1234567890)")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @JsonProperty("email")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @JsonProperty("legalRepresentative")
    @Size(max = 100, message = "Legal representative cannot exceed 100 characters")
    private String legalRepresentative;

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

    // Default constructor
    public UpdateCompanyRequestDTO() {
    }

    // Getters and Setters
    public UUID getTaxId() {
        return taxId;
    }

    public void setTaxId(UUID taxId) {
        this.taxId = taxId;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
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
        return "UpdateCompanyRequestDTO{" +
                "taxId=" + taxId +
                ", nit='" + nit + '\'' +
                ", name='" + name + '\'' +
                ", businessName='" + businessName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}