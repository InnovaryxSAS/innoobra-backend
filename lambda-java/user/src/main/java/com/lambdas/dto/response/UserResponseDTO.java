package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class UserResponseDTO {

    @JsonProperty("idUser")
    private String idUser;

    @JsonProperty("idCompany")
    private String idCompany;

    @JsonProperty("name")
    private String name;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("position")
    private String position;

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

    @JsonProperty("lastAccess")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastAccess;

    // Default constructor
    public UserResponseDTO() {
    }

    // Builder pattern
    public static class Builder {
        private String idUser, idCompany, name, lastName, address, phone, email, position, status;
        private LocalDateTime createdAt, updatedAt, lastAccess;

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

        public Builder position(String position) {
            this.position = position;
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

        public Builder lastAccess(LocalDateTime lastAccess) {
            this.lastAccess = lastAccess;
            return this;
        }

        public UserResponseDTO build() {
            UserResponseDTO dto = new UserResponseDTO();
            dto.idUser = this.idUser;
            dto.idCompany = this.idCompany;
            dto.name = this.name;
            dto.lastName = this.lastName;
            dto.address = this.address;
            dto.phone = this.phone;
            dto.email = this.email;
            dto.position = this.position;
            dto.createdAt = this.createdAt;
            dto.updatedAt = this.updatedAt;
            dto.status = this.status;
            dto.lastAccess = this.lastAccess;
            return dto;
        }
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "idUser='" + idUser + '\'' +
                ", idCompany='" + idCompany + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", lastAccess=" + lastAccess +
                '}';
    }
}