package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateAttributeRequestDTO;
import com.lambdas.dto.request.UpdateAttributeRequestDTO;
import com.lambdas.dto.response.AttributeResponseDTO;
import com.lambdas.model.Attribute;
import com.lambdas.model.AttributeStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static Attribute toAttribute(CreateAttributeRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new Attribute.Builder()
                .idAttribute(dto.getIdAttribute())
                .idCompany(dto.getIdCompany())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .unit(dto.getUnit())
                .status(AttributeStatus.fromValue(dto.getStatus() != null ? dto.getStatus() : "active"))
                .build();
    }

    public static Attribute updateAttributeFromDTO(Attribute existingAttribute, UpdateAttributeRequestDTO dto) {
        if (existingAttribute == null || dto == null) {
            return existingAttribute;
        }

        Attribute.Builder builder = new Attribute.Builder()
                .idAttribute(existingAttribute.getIdAttribute())
                .idCompany(existingAttribute.getIdCompany())
                .createdAt(existingAttribute.getCreatedAt())
                .fromDatabase();

        builder.code(dto.getCode() != null ? dto.getCode() : existingAttribute.getCode());
        builder.name(dto.getName() != null ? dto.getName() : existingAttribute.getName());
        builder.description(dto.getDescription() != null ? dto.getDescription() : existingAttribute.getDescription());
        builder.unit(dto.getUnit() != null ? dto.getUnit() : existingAttribute.getUnit());

        if (dto.getStatus() != null) {
            try {
                AttributeStatus status = AttributeStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingAttribute.getStatus());
            }
        } else {
            builder.status(existingAttribute.getStatus());
        }

        return builder.build();
    }

    public static AttributeResponseDTO toResponseDTO(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        return new AttributeResponseDTO.Builder()
                .idAttribute(attribute.getIdAttribute())
                .idCompany(attribute.getIdCompany())
                .code(attribute.getCode())
                .name(attribute.getName())
                .description(attribute.getDescription())
                .unit(attribute.getUnit())
                .createdAt(attribute.getCreatedAt())
                .updatedAt(attribute.getUpdatedAt())
                .status(attribute.getStatus() != null ? attribute.getStatus().getValue() : null)
                .build();
    }

    public static List<AttributeResponseDTO> toResponseDTOList(List<Attribute> attributes) {
        if (attributes == null) {
            return null;
        }

        return attributes.stream()
                .map(DTOMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}