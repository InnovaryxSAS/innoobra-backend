package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.exception.CompanyAlreadyExistsException;
import com.lambdas.exception.CompanyNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;
import com.lambdas.repository.CompanyRepository;
import com.lambdas.repository.ConnectionPoolManager;

public class CompanyRepositoryImpl implements CompanyRepository {

    private final ConnectionPoolManager poolManager;

    public CompanyRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public CompanyRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public Company save(Company company) {
        if (!existsTaxIdById(company.getTaxId())) {
            throw new ValidationException("Tax ID " + company.getTaxId() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO companies (id, tax_id, nit, name, business_name, company_type, address, phone_number, email,
                                     legal_representative, city, state, country, created_at, updated_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (company.getCreatedAt() == null) {
                company.setCreatedAt(LocalDateTime.now());
            }
            if (company.getUpdatedAt() == null) {
                company.setUpdatedAt(LocalDateTime.now());
            }
            if (company.getStatus() == null) {
                company.setStatus(CompanyStatus.ACTIVE);
            }

            setCompanyParameters(stmt, company);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create company", null);
            }

            return company;

        } catch (SQLException e) {
            e.printStackTrace(); 

            if ("23505".equals(e.getSQLState())) {
                throw new CompanyAlreadyExistsException("Company with ID " + company.getId() + " already exists");
            }
            
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("tax_id")) {
                    throw new ValidationException("Invalid tax ID: " + company.getTaxId());
                }
            }

            throw new DatabaseException("Error creating company: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Company> findById(UUID id) {
        final String sql = """
                SELECT id, tax_id, nit, name, business_name, company_type, address, phone_number, email,
                       legal_representative, city, state, country, created_at, updated_at, status
                FROM companies
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id, java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding company by ID", e);
        }
    }

    @Override
    public List<Company> findAll() {
        final String sql = """
                SELECT id, tax_id, nit, name, business_name, company_type, address, phone_number, email,
                       legal_representative, city, state, country, created_at, updated_at, status
                FROM companies
                ORDER BY created_at DESC
                """;

        List<Company> companies = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }

            return companies;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving companies", e);
        }
    }

    @Override
    public List<Company> findByStatus(CompanyStatus status) {
        final String sql = """
                SELECT id, tax_id, nit, name, business_name, company_type, address, phone_number, email,
                       legal_representative, city, state, country, created_at, updated_at, status
                FROM companies
                WHERE status = ?
                ORDER BY created_at DESC
                """;

        List<Company> companies = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    companies.add(mapResultSetToCompany(rs));
                }
            }

            return companies;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving companies by status", e);
        }
    }

    @Override
    public Company update(Company company) {
        if (!existsTaxIdById(company.getTaxId())) {
            throw new ValidationException("Tax ID " + company.getTaxId() + " does not exist");
        }
        
        final String sql = """
                UPDATE companies
                SET tax_id = ?, nit = ?, name = ?, business_name = ?, company_type = ?, address = ?, phone_number = ?, email = ?,
                    legal_representative = ?, city = ?, state = ?, country = ?, updated_at = ?, status = ?::status_enum
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            company.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, company.getTaxId());
            stmt.setString(2, company.getNit());
            stmt.setString(3, company.getName());
            stmt.setString(4, company.getBusinessName());
            stmt.setString(5, company.getCompanyType());
            stmt.setString(6, company.getAddress());
            stmt.setString(7, company.getPhoneNumber());
            stmt.setString(8, company.getEmail());
            stmt.setString(9, company.getLegalRepresentative());
            stmt.setString(10, company.getCity());
            stmt.setString(11, company.getState());
            stmt.setString(12, company.getCountry());
            stmt.setTimestamp(13, Timestamp.valueOf(company.getUpdatedAt()));
            stmt.setString(14, company.getStatus().getValue());
            stmt.setObject(15, company.getId(), java.sql.Types.OTHER);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new CompanyNotFoundException("Company with ID " + company.getId() + " not found");
            }

            return company;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("tax_id")) {
                    throw new ValidationException("Invalid tax ID: " + company.getTaxId());
                }
            }
            
            throw new DatabaseException("Error updating company", e);
        }
    }

    @Override
    public boolean deactivate(UUID id) {
        final String sql = """
                UPDATE companies
                SET status = CAST(? AS status_enum), updated_at = ?
                WHERE id = ? AND status != CAST(? AS status_enum)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = CompanyStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setObject(3, id, java.sql.Types.OTHER);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(id)) {
                    throw new CompanyNotFoundException("Company with ID " + id + " not found");
                } else {
                    throw new CompanyNotFoundException("Company with ID " + id + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating company: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        final String sql = "SELECT 1 FROM companies WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id, java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if company exists", e);
        }
    }

public boolean existsTaxIdById(String taxId) {
    final String sql = "SELECT 1 FROM taxes WHERE id = ? LIMIT 1";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setObject(1, UUID.fromString(taxId));

        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }

    } catch (SQLException e) {
        throw new DatabaseException("Error checking if tax ID exists", e);
    }
}


    @Override
    public String getConnectionPoolStats() {
        return poolManager.getPoolStats();
    }

    @Override
    public boolean isHealthy() {
        return poolManager.isHealthy();
    }

    private void setCompanyParameters(PreparedStatement stmt, Company company) throws SQLException {
        stmt.setObject(1, company.getId(), java.sql.Types.OTHER);
        stmt.setString(2, company.getTaxId());
        stmt.setString(3, company.getNit());
        stmt.setString(4, company.getName());
        stmt.setString(5, company.getBusinessName());
        stmt.setString(6, company.getCompanyType());
        stmt.setString(7, company.getAddress());
        stmt.setString(8, company.getPhoneNumber());
        stmt.setString(9, company.getEmail());
        stmt.setString(10, company.getLegalRepresentative());
        stmt.setString(11, company.getCity());
        stmt.setString(12, company.getState());
        stmt.setString(13, company.getCountry());
        stmt.setTimestamp(14, Timestamp.valueOf(company.getCreatedAt()));
        stmt.setTimestamp(15, Timestamp.valueOf(company.getUpdatedAt()));
        stmt.setObject(16, company.getStatus().getValue(), java.sql.Types.OTHER);
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company.Builder()
                .id(UUID.fromString(rs.getString("id")))
                .taxId(rs.getString("tax_id"))
                .nit(rs.getString("nit"))
                .name(rs.getString("name"))
                .businessName(rs.getString("business_name"))
                .companyType(rs.getString("company_type"))
                .address(rs.getString("address"))
                .phoneNumber(rs.getString("phone_number"))
                .email(rs.getString("email"))
                .legalRepresentative(rs.getString("legal_representative"))
                .city(rs.getString("city"))
                .state(rs.getString("state"))
                .country(rs.getString("country"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(CompanyStatus.fromValue(rs.getString("status")))
                .fromDatabase()
                .build();
    }
}