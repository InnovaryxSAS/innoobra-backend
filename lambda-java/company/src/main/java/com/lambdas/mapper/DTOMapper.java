package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateCompanyRequestDTO;
import com.lambdas.dto.request.UpdateCompanyRequestDTO;
import com.lambdas.dto.response.CompanyResponseDTO;
import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static Company toCompany(CreateCompanyRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new Company.Builder()
                .id(dto.getId())
                .name(dto.getName())
                .businessName(dto.getBusinessName())
                .companyType(dto.getCompanyType())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .legalRepresentative(dto.getLegalRepresentative())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .status(CompanyStatus.ACTIVE)
                .build();
    }

    public static Company updateCompanyFromDTO(Company existingCompany, UpdateCompanyRequestDTO dto) {
        if (existingCompany == null || dto == null) {
            return existingCompany;
        }

        Company.Builder builder = new Company.Builder()
                .id(existingCompany.getId())
                .createdAt(existingCompany.getCreatedAt())
                .fromDatabase();

        builder.name(dto.getName() != null ? dto.getName() : existingCompany.getName());
        builder.businessName(dto.getBusinessName() != null ? dto.getBusinessName() : existingCompany.getBusinessName());
        builder.companyType(dto.getCompanyType() != null ? dto.getCompanyType() : existingCompany.getCompanyType());
        builder.address(dto.getAddress() != null ? dto.getAddress() : existingCompany.getAddress());
        builder.phoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : existingCompany.getPhoneNumber());
        builder.email(dto.getEmail() != null ? dto.getEmail() : existingCompany.getEmail());
        builder.legalRepresentative(dto.getLegalRepresentative() != null ? dto.getLegalRepresentative()
                : existingCompany.getLegalRepresentative());
        builder.city(dto.getCity() != null ? dto.getCity() : existingCompany.getCity());
        builder.state(dto.getState() != null ? dto.getState() : existingCompany.getState());
        builder.country(dto.getCountry() != null ? dto.getCountry() : existingCompany.getCountry());

        if (dto.getStatus() != null) {
            try {
                CompanyStatus status = CompanyStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingCompany.getStatus());
            }
        } else {
            builder.status(existingCompany.getStatus());
        }

        return builder.build();
    }

    public static Company updateCompanyUsingSetters(Company existingCompany, UpdateCompanyRequestDTO dto) {
        if (existingCompany == null || dto == null) {
            return existingCompany;
        }

        if (dto.getName() != null) {
            existingCompany.setName(dto.getName());
        }
        if (dto.getBusinessName() != null) {
            existingCompany.setBusinessName(dto.getBusinessName());
        }
        if (dto.getCompanyType() != null) {
            existingCompany.setCompanyType(dto.getCompanyType());
        }
        if (dto.getAddress() != null) {
            existingCompany.setAddress(dto.getAddress());
        }
        if (dto.getPhoneNumber() != null) {
            existingCompany.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getEmail() != null) {
            existingCompany.setEmail(dto.getEmail());
        }
        if (dto.getLegalRepresentative() != null) {
            existingCompany.setLegalRepresentative(dto.getLegalRepresentative());
        }
        if (dto.getCity() != null) {
            existingCompany.setCity(dto.getCity());
        }
        if (dto.getState() != null) {
            existingCompany.setState(dto.getState());
        }
        if (dto.getCountry() != null) {
            existingCompany.setCountry(dto.getCountry());
        }
        if (dto.getStatus() != null) {
            try {
                CompanyStatus status = CompanyStatus.fromValue(dto.getStatus());
                existingCompany.setStatus(status);
            } catch (IllegalArgumentException e) {
            }
        }

        return existingCompany;
    }

    public static CompanyResponseDTO toResponseDTO(Company company) {
        if (company == null) {
            return null;
        }

        return new CompanyResponseDTO.Builder()
                .id(company.getId())
                .name(company.getName())
                .businessName(company.getBusinessName())
                .companyType(company.getCompanyType())
                .address(company.getAddress())
                .phoneNumber(company.getPhoneNumber())
                .email(company.getEmail())
                .legalRepresentative(company.getLegalRepresentative())
                .city(company.getCity())
                .state(company.getState())
                .country(company.getCountry())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .status(company.getStatus() != null ? company.getStatus().getValue() : null)
                .build();
    }

    public static List<CompanyResponseDTO> toResponseDTOList(List<Company> companies) {
        if (companies == null) {
            return null;
        }

        return companies.stream()
                .map(DTOMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}