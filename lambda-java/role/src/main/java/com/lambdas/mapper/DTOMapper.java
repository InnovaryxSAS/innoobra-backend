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

        return Role.builder()
                .idRole(dto.getIdRole())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(RoleStatus.ACTIVE)
                .build();
    }

    public static Role updateRoleFromDTO(Role existingRole, UpdateRoleRequestDTO dto) {
        if (existingRole == null || dto == null) {
            return existingRole;
        }

        Role.Builder builder = Role.builder()
                .idRole(existingRole.getIdRole())
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

    public static RoleResponseDTO toRoleResponseDTO(Role role) {
        if (role == null) {
            return null;
        }

        // Fix: Extract the status value separately to avoid type confusion
        String statusValue = null;
        if (role.getStatus() != null) {
            statusValue = role.getStatus().getValue();
        }

        return RoleResponseDTO.builder()
                .idRole(role.getIdRole())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .status(statusValue)
                .build();
    }

    public static List<RoleResponseDTO> toRoleResponseDTOList(List<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(DTOMapper::toRoleResponseDTO)
                .collect(Collectors.toList());
    }
}