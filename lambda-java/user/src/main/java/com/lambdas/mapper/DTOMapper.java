package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateUserRequestDTO;
import com.lambdas.dto.request.UpdateUserRequestDTO;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.model.User;
import com.lambdas.model.UserStatus;
import com.lambdas.util.PasswordUtil;

public class DTOMapper {

    private DTOMapper() {
    }

    public static User toUser(CreateUserRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new User.Builder()
                .id(dto.getId())
                .companyId(dto.getCompanyId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .passwordHash(PasswordUtil.hashPassword(dto.getPassword()))
                .position(dto.getPosition())
                .documentNumber(dto.getDocumentNumber())
                .status(dto.getStatus() != null ? UserStatus.fromValue(dto.getStatus()) : UserStatus.ACTIVE)
                .build();
    }

    public static User updateUserFromDTO(User existingUser, UpdateUserRequestDTO dto) {
        if (existingUser == null || dto == null) {
            return existingUser;
        }

        User.Builder builder = new User.Builder()
                .id(existingUser.getId())
                .companyId(existingUser.getCompanyId())
                .createdAt(existingUser.getCreatedAt())
                .passwordHash(existingUser.getPasswordHash()) // ✅ IMPORTANTE: mantener el password hash existente
                .fromDatabase();

        builder.firstName(dto.getFirstName() != null ? dto.getFirstName() : existingUser.getFirstName());
        builder.lastName(dto.getLastName() != null ? dto.getLastName() : existingUser.getLastName());
        builder.address(dto.getAddress() != null ? dto.getAddress() : existingUser.getAddress());
        builder.phoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : existingUser.getPhoneNumber());
        builder.email(dto.getEmail() != null ? dto.getEmail() : existingUser.getEmail());
        builder.position(dto.getPosition() != null ? dto.getPosition() : existingUser.getPosition());
        builder.documentNumber(dto.getDocumentNumber() != null ? dto.getDocumentNumber() : existingUser.getDocumentNumber());
        builder.lastAccess(existingUser.getLastAccess());

        // Si el DTO incluye una nueva contraseña, hashearla
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            builder.passwordHash(PasswordUtil.hashPassword(dto.getPassword()));
        }

        if (dto.getStatus() != null) {
            try {
                UserStatus status = UserStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingUser.getStatus());
            }
        } else {
            builder.status(existingUser.getStatus());
        }

        return builder.build();
    }

    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDTO.Builder()
                .id(user.getId())
                .companyId(user.getCompanyId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .position(user.getPosition())
                .documentNumber(user.getDocumentNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .status(user.getStatus() != null ? user.getStatus().getValue() : null)
                .lastAccess(user.getLastAccess())
                .build();
    }

    public static List<UserResponseDTO> toResponseDTOList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(DTOMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}