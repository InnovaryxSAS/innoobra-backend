package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {

    @JsonProperty("idUser")
    @NotBlank(message = "User ID cannot be blank")
    @Size(max = 255, message = "User ID cannot exceed 255 characters")
    private String idUser;

    @JsonProperty("idCompany")
    @NotBlank(message = "Company ID cannot be blank")
    @Size(max = 255, message = "Company ID cannot exceed 255 characters")
    private String idCompany;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("lastName")
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @JsonProperty("address")
    @NotBlank(message = "Address cannot be blank")
    @Size(min = 1, max = 100, message = "Address must be between 1 and 100 characters")
    private String address;

    @JsonProperty("phone")
    @NotBlank(message = "Phone cannot be blank")
    @Size(min = 1, max = 20, message = "Phone must be between 1 and 20 characters")
    private String phone;

    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(min = 1, max = 50, message = "Email must be between 1 and 50 characters")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

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
    private UserStatus status;

    @JsonProperty("lastAccess")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastAccess;

    @JsonProperty("position")
    @NotBlank(message = "Position cannot be blank")
    @Size(min = 1, max = 100, message = "Position must be between 1 and 100 characters")
    private String position;

    // Default constructor
    public User() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = UserStatus.ACTIVE;
    }

    // Constructor for existing users (when loading from database)
    public User(String idUser, String idCompany, String name, String lastName, String address,
                String phone, String email, String password, LocalDateTime createdAt,
                LocalDateTime updatedAt, UserStatus status, LocalDateTime lastAccess, String position) {
        this.idUser = idUser;
        this.idCompany = idCompany;
        this.name = name;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.lastAccess = lastAccess;
        this.position = position;
    }

    // Builder pattern
    public static class Builder {
        private String idUser, idCompany, name, lastName, address, phone, email, password, position;
        private LocalDateTime createdAt, updatedAt, lastAccess;
        private UserStatus status;
        private boolean isNewEntity = true;

        public Builder idUser(String idUser) {
            this.idUser = idUser;
            return this;
        }

        public Builder idCompany(String idCompany) {
            this.idCompany = idCompany;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
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

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder lastAccess(LocalDateTime lastAccess) {
            this.lastAccess = lastAccess;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public User build() {
            User user = new User();
            user.idUser = this.idUser;
            user.idCompany = this.idCompany;
            user.name = this.name;
            user.lastName = this.lastName;
            user.address = this.address;
            user.phone = this.phone;
            user.email = this.email;
            user.password = this.password;
            user.position = this.position;
            user.lastAccess = this.lastAccess;
            user.status = this.status != null ? this.status : UserStatus.ACTIVE;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                user.createdAt = now;
                user.updatedAt = now;
            } else {
                user.createdAt = this.createdAt != null ? this.createdAt : now;
                user.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return user;
        }
    }

    // Validation methods
    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            UserStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.trim().length() >= 8;
    }

    public boolean hasRequiredFields() {
        return idUser != null && !idUser.trim().isEmpty() &&
               idCompany != null && !idCompany.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               position != null && !position.trim().isEmpty();
    }

    public void updateLastAccess() {
        this.lastAccess = LocalDateTime.now();
        updateTimestamp();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        if (!Objects.equals(this.idUser, idUser)) {
            this.idUser = idUser;
            updateTimestamp();
        }
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        if (!Objects.equals(this.idCompany, idCompany)) {
            this.idCompany = idCompany;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!Objects.equals(this.lastName, lastName)) {
            this.lastName = lastName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (!Objects.equals(this.phone, phone)) {
            this.phone = phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!Objects.equals(this.password, password)) {
            this.password = password;
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
            updateTimestamp();
        }
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (!Objects.equals(this.position, position)) {
            this.position = position;
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
        User user = (User) o;
        return Objects.equals(idUser, user.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser);
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser='" + idUser + '\'' +
                ", idCompany='" + idCompany + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastAccess=" + lastAccess +
                '}';
    }
}