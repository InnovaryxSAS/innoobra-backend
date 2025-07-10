package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.UserStatusValid;
import jakarta.validation.constraints.*;

public class UpdateUserRequestDTO {

    @JsonProperty("name")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("lastName")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @JsonProperty("address")
    @Size(min = 1, max = 100, message = "Address must be between 1 and 100 characters")
    private String address;

    @JsonProperty("phone")
    @Size(min = 1, max = 20, message = "Phone must be between 1 and 20 characters")
    private String phone;

    @JsonProperty("email")
    @Email(message = "Email must be valid")
    @Size(min = 1, max = 50, message = "Email must be between 1 and 50 characters")
    private String email;

    @JsonProperty("password")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @JsonProperty("position")
    @Size(min = 1, max = 100, message = "Position must be between 1 and 100 characters")
    private String position;

    @JsonProperty("status")
    @UserStatusValid
    private String status;

    // Default constructor
    public UpdateUserRequestDTO() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateUserRequestDTO{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}