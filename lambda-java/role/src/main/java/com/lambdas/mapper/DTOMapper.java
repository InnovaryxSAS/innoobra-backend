package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateRoleRequestDTO;
import com.lambdas.dto.request.UpdateRoleRequestDTO;
import com.lambdas.dto.response.RoleResponseDTO;
import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static Role toRole(CreateRoleRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new Role.Builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? RoleStatus.fromValue(dto.getStatus()) : RoleStatus.ACTIVE)
                .build();
    }

    public static Role updateRoleFromDTO(Role existingRole, UpdateRoleRequestDTO dto) {
        if (existingRole == null || dto == null) {
            return existingRole;
        }

        Role.Builder builder = new Role.Builder()
                .id(existingRole.getId())
                .createdAt(existingRole.getCreatedAt())
                .fromDatabase();

        builder.name(dto.getName() != null ? dto.getName() : existingRole.getName());
        builder.description(dto.getDescription() != null ? dto.getDescription() : existingRole.getDescription());

        if (dto.getStatus() != null) {
            try {
                RoleStatus status = RoleStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingRole.getStatus());
            }
        } else {
            builder.status(existingRole.getStatus());
        }

        return builder.build();
    }

    public static RoleResponseDTO toResponseDTO(Role role) {
        if (role == null) {
            return null;
        }

        return new RoleResponseDTO.Builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .status(role.getStatus() != null ? role.getStatus().getValue() : null)
                .build();
    }

    public static List<RoleResponseDTO> toResponseDTOList(List<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(DTOMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}