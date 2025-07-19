package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.exception.UserAlreadyExistsException;
import com.lambdas.exception.UserNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.User;
import com.lambdas.model.UserStatus;
import com.lambdas.repository.UserRepository;
import com.lambdas.repository.ConnectionPoolManager;

public class UserRepositoryImpl implements UserRepository {

    private final ConnectionPoolManager poolManager;

    public UserRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public UserRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public User save(User user) {
        if (user.getCompanyId() != null && !existsCompanyById(user.getCompanyId())) {
            throw new ValidationException("Company ID " + user.getCompanyId() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO users (id, company_id, first_name, last_name, address, phone_number, email,
                                 password_hash, position, status, last_access, created_at, updated_at, document_number)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::status_enum, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDateTime.now());
            }
            if (user.getUpdatedAt() == null) {
                user.setUpdatedAt(LocalDateTime.now());
            }
            if (user.getStatus() == null) {
                user.setStatus(UserStatus.ACTIVE);
            }

            stmt.setObject(1, user.getId());
            stmt.setObject(2, user.getCompanyId());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getPhoneNumber());
            stmt.setString(7, user.getEmail());
            stmt.setString(8, user.getPasswordHash());
            stmt.setString(9, user.getPosition());
            stmt.setString(10, user.getStatus().getValue());
            stmt.setTimestamp(11, user.getLastAccess() != null ? Timestamp.valueOf(user.getLastAccess()) : null);
            stmt.setTimestamp(12, Timestamp.valueOf(user.getCreatedAt()));
            stmt.setTimestamp(13, Timestamp.valueOf(user.getUpdatedAt()));
            stmt.setString(14, user.getDocumentNumber());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create user", null);
            }

            return user;

        } catch (SQLException e) {
            e.printStackTrace();

            if ("23505".equals(e.getSQLState())) {
                if (e.getMessage().contains("email")) {
                    throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
                } else {
                    throw new UserAlreadyExistsException("User with ID " + user.getId() + " already exists");
                }
            }
            
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("company_id")) {
                    throw new ValidationException("Invalid company ID: " + user.getCompanyId());
                }
            }

            throw new DatabaseException("Error creating user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        final String sql = """
                SELECT id, company_id, first_name, last_name, address, phone_number, email,
                       password_hash, position, status, last_access, created_at, updated_at, document_number
                FROM users
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by ID", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final String sql = """
                SELECT id, company_id, first_name, last_name, address, phone_number, email,
                       password_hash, position, status, last_access, created_at, updated_at, document_number
                FROM users
                WHERE email = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by email", e);
        }
    }

    @Override
    public Optional<User> findByDocumentNumber(String documentNumber) {
        final String sql = """
                SELECT id, company_id, first_name, last_name, address, phone_number, email,
                       password_hash, position, status, last_access, created_at, updated_at, document_number
                FROM users
                WHERE document_number = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documentNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by document number", e);
        }
    }

    @Override
    public List<User> findAll() {
        final String sql = """
                SELECT id, company_id, first_name, last_name, address, phone_number, email,
                       password_hash, position, status, last_access, created_at, updated_at, document_number
                FROM users
                ORDER BY created_at DESC
                """;

        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            return users;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving users", e);
        }
    }

    @Override
    public List<User> findByCompanyId(UUID companyId) {
        final String sql = """
                SELECT id, company_id, first_name, last_name, address, phone_number, email,
                       password_hash, position, status, last_access, created_at, updated_at, document_number
                FROM users
                WHERE company_id = ?
                ORDER BY created_at DESC
                """;

        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, companyId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

            return users;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving users by company ID", e);
        }
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        final String sql = """
                SELECT id, company_id, first_name, last_name, address, phone_number, email,
                       password_hash, position, status, last_access, created_at, updated_at, document_number
                FROM users
                WHERE status = ?::status_enum
                ORDER BY created_at DESC
                """;

        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

            return users;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving users by status", e);
        }
    }

    @Override
    public User update(User user) {
        if (user.getCompanyId() != null && !existsCompanyById(user.getCompanyId())) {
            throw new ValidationException("Company ID " + user.getCompanyId() + " does not exist");
        }
        
        final String sql = """
                UPDATE users
                SET company_id = ?, first_name = ?, last_name = ?, address = ?, phone_number = ?, email = ?,
                    password_hash = ?, position = ?, status = ?::status_enum, last_access = ?, 
                    updated_at = ?, document_number = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            user.setUpdatedAt(LocalDateTime.now());

            stmt.setObject(1, user.getCompanyId());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPasswordHash());
            stmt.setString(8, user.getPosition());
            stmt.setString(9, user.getStatus().getValue());
            stmt.setTimestamp(10, user.getLastAccess() != null ? Timestamp.valueOf(user.getLastAccess()) : null);
            stmt.setTimestamp(11, Timestamp.valueOf(user.getUpdatedAt()));
            stmt.setString(12, user.getDocumentNumber());
            stmt.setObject(13, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("User with ID " + user.getId() + " not found");
            }

            return user;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("company_id")) {
                    throw new ValidationException("Invalid company ID: " + user.getCompanyId());
                }
            }
            
            throw new DatabaseException("Error updating user", e);
        }
    }

    @Override
    public boolean deactivate(UUID id) {
        final String sql = """
                UPDATE users
                SET status = ?::status_enum, updated_at = ?
                WHERE id = ? AND status != ?::status_enum
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = UserStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setObject(3, id);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(id)) {
                    throw new UserNotFoundException("User with ID " + id + " not found");
                } else {
                    throw new UserNotFoundException("User with ID " + id + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        final String sql = "SELECT 1 FROM users WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if user exists", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        final String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if user exists by email", e);
        }
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        final String sql = "SELECT 1 FROM users WHERE document_number = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documentNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if user exists by document number", e);
        }
    }

    @Override
    public boolean updateLastAccess(UUID id) {
        final String sql = """
                UPDATE users
                SET last_access = ?, updated_at = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(1, Timestamp.valueOf(now));
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setObject(3, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("User with ID " + id + " not found");
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating last access: " + e.getMessage(), e);
        }
    }

    public boolean existsCompanyById(UUID companyId) {
        final String sql = "SELECT 1 FROM companies WHERE id = ? LIMIT 1";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, companyId);
            
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

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID companyId = (UUID) rs.getObject("company_id");
        
        return new User.Builder()
                .id(id)
                .companyId(companyId)
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .address(rs.getString("address"))
                .phoneNumber(rs.getString("phone_number"))
                .email(rs.getString("email"))
                .passwordHash(rs.getString("password_hash"))
                .position(rs.getString("position"))
                .documentNumber(rs.getString("document_number"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(UserStatus.fromValue(rs.getString("status")))
                .lastAccess(rs.getTimestamp("last_access") != null ? rs.getTimestamp("last_access").toLocalDateTime() : null)
                .fromDatabase()
                .build();
    }
}