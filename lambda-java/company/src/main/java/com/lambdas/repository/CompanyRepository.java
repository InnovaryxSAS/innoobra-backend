package com.lambdas.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.CompanyAlreadyExistsException;
import com.lambdas.exception.CompanyNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;

public class CompanyRepository {

    private final ConnectionPoolManager poolManager;

    public CompanyRepository() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public CompanyRepository(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    public Company save(Company company) {
        final String sql = """
                INSERT INTO companies (id, name, business_name, company_type, address, phone_number, email,
                                     legal_representative, city, state, country, created_at, updated_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

            throw new DatabaseException("Error creating company: " + e.getMessage(), e);
        }

    }

    public Optional<Company> findById(String id) {
        final String sql = """
                SELECT id, name, business_name, company_type, address, phone_number, email,
                       legal_representative, city, state, country, created_at, updated_at, status
                FROM companies
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

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

    public List<Company> findAll() {
        final String sql = """
                SELECT id, name, business_name, company_type, address, phone_number, email,
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

    public List<Company> findByStatus(CompanyStatus status) {
        final String sql = """
                SELECT id, name, business_name, company_type, address, phone_number, email,
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

    public Company update(Company company) {
        final String sql = """
                UPDATE companies
                SET name = ?, business_name = ?, company_type = ?, address = ?, phone_number = ?, email = ?,
                    legal_representative = ?, city = ?, state = ?, country = ?, updated_at = ?, status = ?::company_status
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            company.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, company.getName());
            stmt.setString(2, company.getBusinessName());
            stmt.setString(3, company.getCompanyType());
            stmt.setString(4, company.getAddress());
            stmt.setString(5, company.getPhoneNumber());
            stmt.setString(6, company.getEmail());
            stmt.setString(7, company.getLegalRepresentative());
            stmt.setString(8, company.getCity());
            stmt.setString(9, company.getState());
            stmt.setString(10, company.getCountry());
            stmt.setTimestamp(11, Timestamp.valueOf(company.getUpdatedAt()));
            stmt.setString(12, company.getStatus().getValue());
            stmt.setString(13, company.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new CompanyNotFoundException("Company with ID " + company.getId() + " not found");
            }

            return company;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating company", e);
        }
    }

    public boolean deactivate(String id) {
        final String sql = """
                UPDATE companies
                SET status = CAST(? AS company_status), updated_at = ?
                WHERE id = ? AND status != CAST(? AS company_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = CompanyStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, id);
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

    public boolean existsById(String id) {
        final String sql = "SELECT 1 FROM companies WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if company exists", e);
        }
    }

    public String getConnectionPoolStats() {
        return poolManager.getPoolStats();
    }

    public boolean isHealthy() {
        return poolManager.isHealthy();
    }

    private void setCompanyParameters(PreparedStatement stmt, Company company) throws SQLException {
        stmt.setString(1, company.getId());
        stmt.setString(2, company.getName());
        stmt.setString(3, company.getBusinessName());
        stmt.setString(4, company.getCompanyType());
        stmt.setString(5, company.getAddress());
        stmt.setString(6, company.getPhoneNumber());
        stmt.setString(7, company.getEmail());
        stmt.setString(8, company.getLegalRepresentative());
        stmt.setString(9, company.getCity());
        stmt.setString(10, company.getState());
        stmt.setString(11, company.getCountry());
        stmt.setTimestamp(12, Timestamp.valueOf(company.getCreatedAt()));
        stmt.setTimestamp(13, Timestamp.valueOf(company.getUpdatedAt()));
        stmt.setObject(14, company.getStatus().getValue(), java.sql.Types.OTHER);
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company.Builder()
                .id(rs.getString("id"))
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