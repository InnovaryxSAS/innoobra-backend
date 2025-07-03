package com.lambdas.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.lambdas.dto.request.CreateCompanyRequestDTO;
import com.lambdas.dto.request.UpdateCompanyRequestDTO;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;

public class ValidationUtil {
    
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,255}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern COUNTRY_PATTERN = Pattern.compile("^[A-Z]{2,3}$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^(active|inactive|pending|suspended)$");

    public static void validateCompanyForCreation(Company company) {
        if (company == null) {
            throw new ValidationException("Company cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        if (company.getId() == null || company.getId().trim().isEmpty()) {
            errors.add("Company ID is required");
        } else if (!ID_PATTERN.matcher(company.getId()).matches()) {
            errors.add("Invalid company ID format");
        }
        
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            errors.add("Company name is required");
        } else if (company.getName().trim().length() > 100) {
            errors.add("Company name cannot exceed 100 characters");
        }
        
        if (company.getBusinessName() == null || company.getBusinessName().trim().isEmpty()) {
            errors.add("Business name is required");
        } else if (company.getBusinessName().trim().length() > 100) {
            errors.add("Business name cannot exceed 100 characters");
        }
        
        if (company.getEmail() == null || company.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!EMAIL_PATTERN.matcher(company.getEmail()).matches()) {
            errors.add("Invalid email format");
        } else if (company.getEmail().length() > 255) {
            errors.add("Email cannot exceed 255 characters");
        }
        
        if (company.getCity() == null || company.getCity().trim().isEmpty()) {
            errors.add("City is required");
        } else if (company.getCity().trim().length() > 50) {
            errors.add("City cannot exceed 50 characters");
        }
        
        if (company.getState() == null || company.getState().trim().isEmpty()) {
            errors.add("State is required");
        } else if (company.getState().trim().length() > 100) {
            errors.add("State cannot exceed 100 characters");
        }
        
        if (company.getCountry() == null || company.getCountry().trim().isEmpty()) {
            errors.add("Country is required");
        } else if (!COUNTRY_PATTERN.matcher(company.getCountry()).matches()) {
            errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
        }
        
        if (company.getCompanyType() != null && company.getCompanyType().length() > 100) {
            errors.add("Company type cannot exceed 100 characters");
        }
        
        if (company.getAddress() != null && company.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }
        
        if (company.getPhoneNumber() != null && !company.getPhoneNumber().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(company.getPhoneNumber()).matches()) {
                errors.add("Phone number must follow E.164 format (+country code + number)");
            }
        }
        
        if (company.getLegalRepresentative() != null && company.getLegalRepresentative().length() > 100) {
            errors.add("Legal representative cannot exceed 100 characters");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }
    
    public static void validateCompanyForUpdate(Company company) {
        if (company == null) {
            throw new ValidationException("Company cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        if (company.getId() == null || company.getId().trim().isEmpty()) {
            errors.add("Company ID is required for update");
        }
        
        if (company.getName() != null) {
            if (company.getName().trim().isEmpty()) {
                errors.add("Company name cannot be empty");
            } else if (company.getName().trim().length() > 100) {
                errors.add("Company name cannot exceed 100 characters");
            }
        }
        
        if (company.getBusinessName() != null) {
            if (company.getBusinessName().trim().isEmpty()) {
                errors.add("Business name cannot be empty");
            } else if (company.getBusinessName().trim().length() > 100) {
                errors.add("Business name cannot exceed 100 characters");
            }
        }
        
        if (company.getEmail() != null && !company.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(company.getEmail()).matches()) {
                errors.add("Invalid email format");
            } else if (company.getEmail().length() > 255) {
                errors.add("Email cannot exceed 255 characters");
            }
        }
        
        if (company.getCity() != null) {
            if (company.getCity().trim().isEmpty()) {
                errors.add("City cannot be empty");
            } else if (company.getCity().trim().length() > 50) {
                errors.add("City cannot exceed 50 characters");
            }
        }
        
        if (company.getState() != null) {
            if (company.getState().trim().isEmpty()) {
                errors.add("State cannot be empty");
            } else if (company.getState().trim().length() > 100) {
                errors.add("State cannot exceed 100 characters");
            }
        }
        
        if (company.getCountry() != null && !company.getCountry().trim().isEmpty()) {
            if (!COUNTRY_PATTERN.matcher(company.getCountry()).matches()) {
                errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
            }
        }
        
        if (company.getCompanyType() != null && company.getCompanyType().length() > 100) {
            errors.add("Company type cannot exceed 100 characters");
        }
        
        if (company.getAddress() != null && company.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }
        
        if (company.getPhoneNumber() != null && !company.getPhoneNumber().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(company.getPhoneNumber()).matches()) {
                errors.add("Phone number must follow E.164 format (+country code + number)");
            }
        }
        
        if (company.getLegalRepresentative() != null && company.getLegalRepresentative().length() > 100) {
            errors.add("Legal representative cannot exceed 100 characters");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }

    public static ValidationResult validateCreateRequest(CreateCompanyRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getId() == null || dto.getId().trim().isEmpty()) {
            errors.add("Company ID is required");
        } else if (!ID_PATTERN.matcher(dto.getId()).matches()) {
            errors.add("Invalid company ID format");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.add("Company name is required");
        } else if (dto.getName().trim().length() > 100) {
            errors.add("Company name cannot exceed 100 characters");
        }

        if (dto.getBusinessName() == null || dto.getBusinessName().trim().isEmpty()) {
            errors.add("Business name is required");
        } else if (dto.getBusinessName().trim().length() > 100) {
            errors.add("Business name cannot exceed 100 characters");
        }

        if (dto.getCompanyType() != null && dto.getCompanyType().length() > 100) {
            errors.add("Company type cannot exceed 100 characters");
        }

        if (dto.getAddress() != null && dto.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(dto.getPhoneNumber()).matches()) {
                errors.add("Phone number must follow E.164 format (+country code + number)");
            }
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        } else {
            if (dto.getEmail().length() > 255) {
                errors.add("Email cannot exceed 255 characters");
            }
            if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
                errors.add("Invalid email format");
            }
        }

        if (dto.getLegalRepresentative() != null && dto.getLegalRepresentative().length() > 100) {
            errors.add("Legal representative cannot exceed 100 characters");
        }

        if (dto.getCity() == null || dto.getCity().trim().isEmpty()) {
            errors.add("City is required");
        } else if (dto.getCity().trim().length() > 50) {
            errors.add("City cannot exceed 50 characters");
        }

        if (dto.getState() == null || dto.getState().trim().isEmpty()) {
            errors.add("State is required");
        } else if (dto.getState().trim().length() > 100) {
            errors.add("State cannot exceed 100 characters");
        }

        if (dto.getCountry() == null || dto.getCountry().trim().isEmpty()) {
            errors.add("Country is required");
        } else {
            if (!COUNTRY_PATTERN.matcher(dto.getCountry()).matches()) {
                errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult validateUpdateRequest(UpdateCompanyRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getName() != null) {
            if (dto.getName().trim().isEmpty()) {
                errors.add("Company name cannot be empty");
            } else if (dto.getName().trim().length() > 100) {
                errors.add("Company name cannot exceed 100 characters");
            }
        }

        if (dto.getBusinessName() != null) {
            if (dto.getBusinessName().trim().isEmpty()) {
                errors.add("Business name cannot be empty");
            } else if (dto.getBusinessName().trim().length() > 100) {
                errors.add("Business name cannot exceed 100 characters");
            }
        }

        if (dto.getCompanyType() != null && dto.getCompanyType().length() > 100) {
            errors.add("Company type cannot exceed 100 characters");
        }

        if (dto.getAddress() != null && dto.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(dto.getPhoneNumber()).matches()) {
                errors.add("Phone number must follow E.164 format (+country code + number)");
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (dto.getEmail().length() > 255) {
                errors.add("Email cannot exceed 255 characters");
            }
            if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
                errors.add("Invalid email format");
            }
        }

        if (dto.getLegalRepresentative() != null && dto.getLegalRepresentative().length() > 100) {
            errors.add("Legal representative cannot exceed 100 characters");
        }

        if (dto.getCity() != null) {
            if (dto.getCity().trim().isEmpty()) {
                errors.add("City cannot be empty");
            } else if (dto.getCity().trim().length() > 50) {
                errors.add("City cannot exceed 50 characters");
            }
        }

        if (dto.getState() != null) {
            if (dto.getState().trim().isEmpty()) {
                errors.add("State cannot be empty");
            } else if (dto.getState().trim().length() > 100) {
                errors.add("State cannot exceed 100 characters");
            }
        }

        if (dto.getCountry() != null && !dto.getCountry().trim().isEmpty()) {
            if (!COUNTRY_PATTERN.matcher(dto.getCountry()).matches()) {
                errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
            }
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            if (!STATUS_PATTERN.matcher(dto.getStatus().toLowerCase()).matches()) {
                errors.add("Status must be one of: active, inactive, pending, suspended");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorsAsString() {
            return String.join(", ", errors);
        }
    }

    public static boolean isValidStatus(String status) {
        if (status == null) return false;
        return STATUS_PATTERN.matcher(status.toLowerCase()).matches();
    }

    public static CompanyStatus parseStatus(String status) {
        if (status == null) return null;
        return CompanyStatus.fromValue(status.toLowerCase());
    }
}