package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.UserStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

public class CreateUserRequestDTO {

    @JsonProperty("idUser")
    @NotBlank(message = "User ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "User ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]{1,255}$", message = "User ID must contain only alphanumeric characters, underscores, and hyphens")
    private String idUser;

    @JsonProperty("idCompany")
    @NotBlank(message = "Company ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Company ID cannot exceed 255 characters")
    private String idCompany;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("lastName")
    @NotBlank(message = "Last name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @JsonProperty("address")
    @NotBlank(message = "Address cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Address must be between 1 and 100 characters")
    private String address;

    @JsonProperty("phone")
    @NotBlank(message = "Phone cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 20, message = "Phone must be between 1 and 20 characters")
    private String phone;

    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank", groups = ValidationGroups.Create.class)
    @Email(message = "Email must be valid")
    @Size(min = 1, max = 50, message = "Email must be between 1 and 50 characters")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @JsonProperty("position")
    @NotBlank(message = "Position cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Position must be between 1 and 100 characters")
    private String position;

    @JsonProperty("status")
    @UserStatusValid
    private String status = "active";

    // Default constructor
    public CreateUserRequestDTO() {
    }

    // Getters and Setters
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
    }

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
        return "CreateUserRequestDTO{" +
                "idUser='" + idUser + '\'' +
                ", idCompany='" + idCompany + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}