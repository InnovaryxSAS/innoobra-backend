package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateUserRequestDTO;
import com.lambdas.dto.request.UpdateUserRequestDTO;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.model.User;
import com.lambdas.model.UserStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static User toUser(CreateUserRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new User.Builder()
                .idUser(dto.getIdUser())
                .idCompany(dto.getIdCompany())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .position(dto.getPosition())
                .status(UserStatus.fromValue(dto.getStatus() != null ? dto.getStatus() : "active"))
                .build();
    }

    public static User updateUserFromDTO(User existingUser, UpdateUserRequestDTO dto) {
        if (existingUser == null || dto == null) {
            return existingUser;
        }

        User.Builder builder = new User.Builder()
                .idUser(existingUser.getIdUser())
                .idCompany(existingUser.getIdCompany())
                .createdAt(existingUser.getCreatedAt())
                .fromDatabase();

        builder.name(dto.getName() != null ? dto.getName() : existingUser.getName());
        builder.lastName(dto.getLastName() != null ? dto.getLastName() : existingUser.getLastName());
        builder.address(dto.getAddress() != null ? dto.getAddress() : existingUser.getAddress());
        builder.phone(dto.getPhone() != null ? dto.getPhone() : existingUser.getPhone());
        builder.email(dto.getEmail() != null ? dto.getEmail() : existingUser.getEmail());
        builder.password(dto.getPassword() != null ? dto.getPassword() : existingUser.getPassword());
        builder.position(dto.getPosition() != null ? dto.getPosition() : existingUser.getPosition());
        builder.lastAccess(existingUser.getLastAccess());

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
                .idUser(user.getIdUser())
                .idCompany(user.getIdCompany())
                .name(user.getName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .email(user.getEmail())
                .position(user.getPosition())
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