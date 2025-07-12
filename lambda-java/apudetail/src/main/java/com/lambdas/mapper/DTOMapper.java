package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateApuDetailRequestDTO;
import com.lambdas.dto.request.UpdateApuDetailRequestDTO;
import com.lambdas.dto.response.ApuDetailResponseDTO;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.model.ApuDetail;
import com.lambdas.model.ApuDetailStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static ApuDetail toApuDetail(CreateApuDetailRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new ApuDetail.Builder()
                .idApuDetail(dto.getIdApuDetail())
                .idActivity(dto.getIdActivity())
                .idAttribute(dto.getIdAttribute())
                .quantity(dto.getQuantity())
                .wastePercentage(dto.getWastePercentage() != null ? dto.getWastePercentage() : 0.0)
                .status(ApuDetailStatus.fromValue(dto.getStatus() != null ? dto.getStatus() : "active"))
                .build();
    }

    public static ApuDetail updateApuDetailFromDTO(ApuDetail existingApuDetail, UpdateApuDetailRequestDTO dto) {
        if (existingApuDetail == null || dto == null) {
            return existingApuDetail;
        }

        ApuDetail.Builder builder = new ApuDetail.Builder()
                .idApuDetail(existingApuDetail.getIdApuDetail())
                .idActivity(existingApuDetail.getIdActivity())
                .idAttribute(existingApuDetail.getIdAttribute())
                .createdAt(existingApuDetail.getCreatedAt())
                .fromDatabase();

        builder.quantity(dto.getQuantity() != null ? dto.getQuantity() : existingApuDetail.getQuantity());
        builder.wastePercentage(dto.getWastePercentage() != null ? dto.getWastePercentage() : existingApuDetail.getWastePercentage());

        if (dto.getStatus() != null) {
            try {
                ApuDetailStatus status = ApuDetailStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingApuDetail.getStatus());
            }
        } else {
            builder.status(existingApuDetail.getStatus());
        }

        return builder.build();
    }

    public static ApuDetailResponseDTO toResponseDTO(ApuDetail apuDetail) {
        if (apuDetail == null) {
            return null;
        }

        return new ApuDetailResponseDTO.Builder()
                .idApuDetail(apuDetail.getIdApuDetail())
                .idActivity(apuDetail.getIdActivity())
                .idAttribute(apuDetail.getIdAttribute())
                .quantity(apuDetail.getQuantity())
                .wastePercentage(apuDetail.getWastePercentage())
                .createdAt(apuDetail.getCreatedAt())
                .updatedAt(apuDetail.getUpdatedAt())
                .status(apuDetail.getStatus() != null ? apuDetail.getStatus().getValue() : null)
                .build();
    }

    public static List<ApuDetailResponseDTO> toResponseDTOList(List<ApuDetail> apuDetails) {
        if (apuDetails == null) {
            return null;
        }

        return apuDetails.stream()
                .map(DTOMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public static DeleteResponseDTO toDeleteResponseDTO(String apuDetailId, boolean success, String message) {
        return new DeleteResponseDTO.Builder()
                .apuDetailId(apuDetailId)
                .success(success)
                .message(message)
                .build();
    }
}