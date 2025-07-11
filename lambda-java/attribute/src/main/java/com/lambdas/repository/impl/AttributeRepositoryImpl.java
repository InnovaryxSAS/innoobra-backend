package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.AttributeAlreadyExistsException;
import com.lambdas.exception.AttributeNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.Attribute;
import com.lambdas.model.AttributeStatus;
import com.lambdas.repository.AttributeRepository;
import com.lambdas.repository.ConnectionPoolManager;

public class AttributeRepositoryImpl implements AttributeRepository {

    private final ConnectionPoolManager poolManager;

    public AttributeRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public AttributeRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public Attribute save(Attribute attribute) {
        if (!existsCompanyById(attribute.getIdCompany())) {
            throw new ValidationException("Company with ID " + attribute.getIdCompany() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO attributes (id_attribute, id_company, code, name, description, unit,
                                     created_at, updated_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (attribute.getCreatedAt() == null) {
                attribute.setCreatedAt(LocalDateTime.now());
            }
            if (attribute.getUpdatedAt() == null) {
                attribute.setUpdatedAt(LocalDateTime.now());
            }
            if (attribute.getStatus() == null) {
                attribute.setStatus(AttributeStatus.ACTIVE);
            }

            setAttributeParameters(stmt, attribute);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create attribute", null);
            }

            return attribute;

        } catch (SQLException e) {
            e.printStackTrace();

            if ("23505".equals(e.getSQLState())) {
                if (e.getMessage().contains("code")) {
                    throw new AttributeAlreadyExistsException("Attribute with code " + attribute.getCode() + " already exists");
                } else {
                    throw new AttributeAlreadyExistsException("Attribute with ID " + attribute.getIdAttribute() + " already exists");
                }
            }
            
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid company ID: " + attribute.getIdCompany());
            }

            throw new DatabaseException("Error creating attribute: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Attribute> findById(String idAttribute) {
        final String sql = """
                SELECT id_attribute, id_company, code, name, description, unit,
                       created_at, updated_at, status
                FROM attributes
                WHERE id_attribute = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idAttribute);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttribute(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding attribute by ID", e);
        }
    }

    @Override
    public Optional<Attribute> findByCode(String code) {
        final String sql = """
                SELECT id_attribute, id_company, code, name, description, unit,
                       created_at, updated_at, status
                FROM attributes
                WHERE code = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttribute(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding attribute by code", e);
        }
    }

    @Override
    public List<Attribute> findAll() {
        final String sql = """
                SELECT id_attribute, id_company, code, name, description, unit,
                       created_at, updated_at, status
                FROM attributes
                ORDER BY created_at DESC
                """;

        List<Attribute> attributes = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                attributes.add(mapResultSetToAttribute(rs));
            }

            return attributes;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving attributes", e);
        }
    }

    @Override
    public List<Attribute> findByCompanyId(String idCompany) {
        final String sql = """
                SELECT id_attribute, id_company, code, name, description, unit,
                       created_at, updated_at, status
                FROM attributes
                WHERE id_company = ?
                ORDER BY created_at DESC
                """;

        List<Attribute> attributes = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idCompany);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attributes.add(mapResultSetToAttribute(rs));
                }
                return attributes;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving attributes by company ID", e);
        }
    }

    @Override
    public List<Attribute> findByStatus(AttributeStatus status) {
        final String sql = """
                SELECT id_attribute, id_company, code, name, description, unit,
                       created_at, updated_at, status
                FROM attributes
                WHERE status = ?
                ORDER BY created_at DESC
                """;

        List<Attribute> attributes = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attributes.add(mapResultSetToAttribute(rs));
                }
                return attributes;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving attributes by status", e);
        }
    }

    @Override
    public Attribute update(Attribute attribute) {
        if (!existsCompanyById(attribute.getIdCompany())) {
            throw new ValidationException("Company with ID " + attribute.getIdCompany() + " does not exist");
        }
        
        final String sql = """
                UPDATE attributes
                SET id_company = ?, code = ?, name = ?, description = ?, unit = ?,
                    updated_at = ?, status = ?::attribute_status
                WHERE id_attribute = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            attribute.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, attribute.getIdCompany());
            stmt.setString(2, attribute.getCode());
            stmt.setString(3, attribute.getName());
            stmt.setString(4, attribute.getDescription());
            stmt.setString(5, attribute.getUnit());
            stmt.setTimestamp(6, Timestamp.valueOf(attribute.getUpdatedAt()));
            stmt.setString(7, attribute.getStatus().getValue());
            stmt.setString(8, attribute.getIdAttribute());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new AttributeNotFoundException("Attribute with ID " + attribute.getIdAttribute() + " not found");
            }

            return attribute;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid company ID: " + attribute.getIdCompany());
            }
            
            if ("23505".equals(e.getSQLState()) && e.getMessage().contains("code")) {
                throw new AttributeAlreadyExistsException("Attribute with code " + attribute.getCode() + " already exists");
            }
            
            throw new DatabaseException("Error updating attribute", e);
        }
    }

    @Override
    public boolean deactivate(String idAttribute) {
        final String sql = """
                UPDATE attributes
                SET status = CAST(? AS attribute_status), updated_at = ?
                WHERE id_attribute = ? AND status != CAST(? AS attribute_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = AttributeStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idAttribute);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(idAttribute)) {
                    throw new AttributeNotFoundException("Attribute with ID " + idAttribute + " not found");
                } else {
                    throw new AttributeNotFoundException("Attribute with ID " + idAttribute + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating attribute: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(String idAttribute) {
        final String sql = "SELECT 1 FROM attributes WHERE id_attribute = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idAttribute);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if attribute exists", e);
        }
    }

    @Override
    public boolean existsByCode(String code) {
        final String sql = "SELECT 1 FROM attributes WHERE code = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if attribute exists by code", e);
        }
    }

    public boolean existsCompanyById(String idCompany) {
        final String sql = "SELECT 1 FROM companies WHERE id = ? LIMIT 1";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idCompany);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error checking if company exists", e);
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

    private void setAttributeParameters(PreparedStatement stmt, Attribute attribute) throws SQLException {
        stmt.setString(1, attribute.getIdAttribute());
        stmt.setString(2, attribute.getIdCompany());
        stmt.setString(3, attribute.getCode());
        stmt.setString(4, attribute.getName());
        stmt.setString(5, attribute.getDescription());
        stmt.setString(6, attribute.getUnit());
        stmt.setTimestamp(7, Timestamp.valueOf(attribute.getCreatedAt()));
        stmt.setTimestamp(8, Timestamp.valueOf(attribute.getUpdatedAt()));
        stmt.setObject(9, attribute.getStatus().getValue(), java.sql.Types.OTHER);
    }

    private Attribute mapResultSetToAttribute(ResultSet rs) throws SQLException {
        return new Attribute.Builder()
                .idAttribute(rs.getString("id_attribute"))
                .idCompany(rs.getString("id_company"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .unit(rs.getString("unit"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(AttributeStatus.fromValue(rs.getString("status")))
                .fromDatabase()
                .build();
    }
}