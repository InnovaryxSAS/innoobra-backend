package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {

    @JsonProperty("id")
    // ID es autogenerado por la base de datos, no requiere validación de entrada
    private UUID id;

    @JsonProperty("companyId")
    @NotNull(message = "Company ID cannot be null")
    private UUID companyId;

    @JsonProperty("firstName")
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @JsonProperty("address")
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String address;

    @JsonProperty("phoneNumber")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(min = 1, max = 50, message = "Email must be between 1 and 50 characters")
    private String email;

    @JsonProperty("passwordHash")
    @NotBlank(message = "Password hash cannot be blank")
    private String passwordHash;

    @JsonProperty("position")
    @Size(max = 100, message = "Position cannot exceed 100 characters")
    private String position;

    @JsonProperty("status")
    @NotNull(message = "Status cannot be null")
    private UserStatus status;

    @JsonProperty("lastAccess")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastAccess;

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

    @JsonProperty("documentNumber")
    @NotBlank(message = "Document number cannot be blank")
    @Size(min = 1, max = 30, message = "Document number must be between 1 and 30 characters")
    private String documentNumber;

    // Default constructor
    public User() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = UserStatus.ACTIVE;
    }

    // Constructor for existing users (when loading from database)
    public User(UUID id, UUID companyId, String firstName, String lastName, String address,
                String phoneNumber, String email, String passwordHash, String position,
                UserStatus status, LocalDateTime lastAccess, LocalDateTime createdAt,
                LocalDateTime updatedAt, String documentNumber) {
        this.id = id;
        this.companyId = companyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.position = position;
        this.status = status;
        this.lastAccess = lastAccess;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.documentNumber = documentNumber;
    }

    // Builder pattern
    public static class Builder {
        private UUID id, companyId;
        private String firstName, lastName, address, phoneNumber, email, passwordHash, position, documentNumber;
        private LocalDateTime createdAt, updatedAt, lastAccess;
        private UserStatus status;
        private boolean isNewEntity = true;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder companyId(UUID companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
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

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
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
            user.id = this.id != null ? this.id : UUID.randomUUID();
            user.companyId = this.companyId;
            user.firstName = this.firstName;
            user.lastName = this.lastName;
            user.address = this.address;
            user.phoneNumber = this.phoneNumber;
            user.email = this.email;
            user.passwordHash = this.passwordHash;
            user.position = this.position;
            user.documentNumber = this.documentNumber;
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

    public boolean isValidDocumentNumber(String documentNumber) {
        return documentNumber != null && !documentNumber.trim().isEmpty() && documentNumber.trim().length() <= 30;
    }

    public boolean hasRequiredFields() {
        return companyId != null &&
               firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               passwordHash != null && !passwordHash.trim().isEmpty() &&
               documentNumber != null && !documentNumber.trim().isEmpty();
    }

    public void updateLastAccess() {
        this.lastAccess = LocalDateTime.now();
        updateTimestamp();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
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
        if (!Objects.equals(this.companyId, companyId)) {
            this.companyId = companyId;
            updateTimestamp();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!Objects.equals(this.firstName, firstName)) {
            this.firstName = firstName;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (!Objects.equals(this.passwordHash, passwordHash)) {
            this.passwordHash = passwordHash;
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

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        if (!Objects.equals(this.documentNumber, documentNumber)) {
            this.documentNumber = documentNumber;
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
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", status=" + status +
                ", documentNumber='" + documentNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastAccess=" + lastAccess +
                '}';
    }
}