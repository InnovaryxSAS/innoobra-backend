package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lambdas.validation.annotations.StatusValid;
import jakarta.validation.constraints.*;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequestDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("companyId")
    private UUID companyId;

    @JsonProperty("firstName")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @JsonProperty("lastName")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @JsonProperty("address")
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String address;

    @JsonProperty("phoneNumber")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @JsonProperty("email")
    @Email(message = "Email must be valid")
    @Size(min = 1, max = 50, message = "Email must be between 1 and 50 characters")
    private String email;

    @JsonProperty("password")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @JsonProperty("position")
    @Size(max = 100, message = "Position cannot exceed 100 characters")
    private String position;

    @JsonProperty("documentNumber")
    @Size(min = 1, max = 30, message = "Document number must be between 1 and 30 characters")
    private String documentNumber;

    @JsonProperty("status")
    @StatusValid
    private String status;

    // Default constructor
    public UpdateUserRequestDTO() {
    }

    // Constructor for debugging
    public UpdateUserRequestDTO(UUID id, String firstName, String lastName, String email, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateUserRequestDTO{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}