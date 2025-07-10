package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.UserAlreadyExistsException;
import com.lambdas.exception.UserNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.model.User;
import com.lambdas.model.UserStatus;
import com.lambdas.repository.UserRepository;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.exception.ValidationException;

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
        if (!existsCompanyById(user.getIdCompany())) {
            throw new ValidationException("Company with ID " + user.getIdCompany() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO users (id_user, id_company, name, last_name, address, phone, email,
                                password, created_at, updated_at, status, last_access, position)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

            setUserParameters(stmt, user);

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
                    throw new UserAlreadyExistsException("User with ID " + user.getIdUser() + " already exists");
                }
            }
            
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid company ID: " + user.getIdCompany());
            }

            throw new DatabaseException("Error creating user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(String idUser) {
        final String sql = """
                SELECT id_user, id_company, name, last_name, address, phone, email,
                       password, created_at, updated_at, status, last_access, position
                FROM users
                WHERE id_user = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idUser);

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
                SELECT id_user, id_company, name, last_name, address, phone, email,
                       password, created_at, updated_at, status, last_access, position
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
    public List<User> findAll() {
        final String sql = """
                SELECT id_user, id_company, name, last_name, address, phone, email,
                       password, created_at, updated_at, status, last_access, position
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
    public User update(User user) {
        if (!existsCompanyById(user.getIdCompany())) {
            throw new ValidationException("Company with ID " + user.getIdCompany() + " does not exist");
        }
        
        final String sql = """
                UPDATE users
                SET id_company = ?, name = ?, last_name = ?, address = ?, phone = ?, email = ?,
                    password = ?, updated_at = ?, status = ?::user_status, last_access = ?, position = ?
                WHERE id_user = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            user.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, user.getIdCompany());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPassword());
            stmt.setTimestamp(8, Timestamp.valueOf(user.getUpdatedAt()));
            stmt.setString(9, user.getStatus().getValue());
            stmt.setTimestamp(10, user.getLastAccess() != null ? Timestamp.valueOf(user.getLastAccess()) : null);
            stmt.setString(11, user.getPosition());
            stmt.setString(12, user.getIdUser());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("User with ID " + user.getIdUser() + " not found");
            }

            return user;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid company ID: " + user.getIdCompany());
            }
            
            throw new DatabaseException("Error updating user", e);
        }
    }

    @Override
    public boolean deactivate(String idUser) {
        final String sql = """
                UPDATE users
                SET status = CAST(? AS user_status), updated_at = ?
                WHERE id_user = ? AND status != CAST(? AS user_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = UserStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idUser);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(idUser)) {
                    throw new UserNotFoundException("User with ID " + idUser + " not found");
                } else {
                    throw new UserNotFoundException("User with ID " + idUser + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(String idUser) {
        final String sql = "SELECT 1 FROM users WHERE id_user = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idUser);

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
    public boolean updateLastAccess(String idUser) {
        final String sql = """
                UPDATE users
                SET last_access = ?, updated_at = ?
                WHERE id_user = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(1, Timestamp.valueOf(now));
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idUser);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("User with ID " + idUser + " not found");
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating last access: " + e.getMessage(), e);
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

    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getIdUser());
        stmt.setString(2, user.getIdCompany());
        stmt.setString(3, user.getName());
        stmt.setString(4, user.getLastName());
        stmt.setString(5, user.getAddress());
        stmt.setString(6, user.getPhone());
        stmt.setString(7, user.getEmail());
        stmt.setString(8, user.getPassword());
        stmt.setTimestamp(9, Timestamp.valueOf(user.getCreatedAt()));
        stmt.setTimestamp(10, Timestamp.valueOf(user.getUpdatedAt()));
        stmt.setObject(11, user.getStatus().getValue(), java.sql.Types.OTHER);
        stmt.setTimestamp(12, user.getLastAccess() != null ? Timestamp.valueOf(user.getLastAccess()) : null);
        stmt.setString(13, user.getPosition());
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User.Builder()
                .idUser(rs.getString("id_user"))
                .idCompany(rs.getString("id_company"))
                .name(rs.getString("name"))
                .lastName(rs.getString("last_name"))
                .address(rs.getString("address"))
                .phone(rs.getString("phone"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(UserStatus.fromValue(rs.getString("status")))
                .lastAccess(rs.getTimestamp("last_access") != null ? rs.getTimestamp("last_access").toLocalDateTime() : null)
                .position(rs.getString("position"))
                .fromDatabase()
                .build();
    }
}