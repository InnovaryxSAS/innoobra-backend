package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.RoleAlreadyExistsException;
import com.lambdas.exception.RoleNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;
import com.lambdas.repository.RoleRepository;
import com.lambdas.repository.ConnectionPoolManager;

public class RoleRepositoryImpl implements RoleRepository{

    private final ConnectionPoolManager poolManager;

    public RoleRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public RoleRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public Role save(Role role) {
        final String sql = """
                INSERT INTO roles (id_role, name, description, created_at, updated_at, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (role.getCreatedAt() == null) {
                role.setCreatedAt(LocalDateTime.now());
            }
            if (role.getUpdatedAt() == null) {
                role.setUpdatedAt(LocalDateTime.now());
            }
            if (role.getStatus() == null) {
                role.setStatus(RoleStatus.ACTIVE);
            }

            setRoleParameters(stmt, role);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create role", null);
            }

            return role;

        } catch (SQLException e) {
            e.printStackTrace(); 

            if ("23505".equals(e.getSQLState())) {
                throw new RoleAlreadyExistsException("Role with ID " + role.getIdRole() + " already exists");
            }

            throw new DatabaseException("Error creating role: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Role> findById(String idRole) {
        final String sql = """
                SELECT id_role, name, description, created_at, updated_at, status
                FROM roles
                WHERE id_role = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idRole);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRole(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding role by ID", e);
        }
    }

    @Override
    public List<Role> findAll() {
        final String sql = """
                SELECT id_role, name, description, created_at, updated_at, status
                FROM roles
                ORDER BY created_at DESC
                """;

        List<Role> roles = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }

            return roles;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving roles", e);
        }
    }

    @Override
    public List<Role> findByStatus(RoleStatus status) {
        final String sql = """
                SELECT id_role, name, description, created_at, updated_at, status
                FROM roles
                WHERE status = ?
                ORDER BY created_at DESC
                """;

        List<Role> roles = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(mapResultSetToRole(rs));
                }
            }

            return roles;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving roles by status", e);
        }
    }

    @Override
    public List<Role> findByName(String name) {
        final String sql = """
                SELECT id_role, name, description, created_at, updated_at, status
                FROM roles
                WHERE name ILIKE ?
                ORDER BY created_at DESC
                """;

        List<Role> roles = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(mapResultSetToRole(rs));
                }
            }

            return roles;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving roles by name", e);
        }
    }

    @Override
    public Role update(Role role) {
        final String sql = """
                UPDATE roles
                SET name = ?, description = ?, updated_at = ?, status = ?::role_status
                WHERE id_role = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            role.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(role.getUpdatedAt()));
            stmt.setString(4, role.getStatus().getValue());
            stmt.setString(5, role.getIdRole());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RoleNotFoundException("Role with ID " + role.getIdRole() + " not found");
            }

            return role;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating role", e);
        }
    }

    @Override
    public boolean deactivate(String idRole) {
        final String sql = """
                UPDATE roles
                SET status = CAST(? AS role_status), updated_at = ?
                WHERE id_role = ? AND status != CAST(? AS role_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = RoleStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idRole);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(idRole)) {
                    throw new RoleNotFoundException("Role with ID " + idRole + " not found");
                } else {
                    throw new RoleNotFoundException("Role with ID " + idRole + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating role: " + e.getMessage(), e);
        }
    }


    public long count() {
        final String sql = "SELECT COUNT(*) FROM roles";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting roles", e);
        }
    }

    public long countByStatus(RoleStatus status) {
        final String sql = "SELECT COUNT(*) FROM roles WHERE status = ?";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error counting roles by status", e);
        }
    }

    public boolean existsById(String idRole) {
        final String sql = "SELECT 1 FROM roles WHERE id_role = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idRole);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if role exists", e);
        }
    }

    public boolean existsByName(String name) {
        final String sql = "SELECT 1 FROM roles WHERE name = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if role name exists", e);
        }
    }

    public String getConnectionPoolStats() {
        return poolManager.getPoolStats();
    }

    public boolean isHealthy() {
        return poolManager.isHealthy();
    }

    private void setRoleParameters(PreparedStatement stmt, Role role) throws SQLException {
        stmt.setString(1, role.getIdRole());
        stmt.setString(2, role.getName());
        stmt.setString(3, role.getDescription());
        stmt.setTimestamp(4, Timestamp.valueOf(role.getCreatedAt()));
        stmt.setTimestamp(5, Timestamp.valueOf(role.getUpdatedAt()));
        stmt.setObject(6, role.getStatus().getValue(), java.sql.Types.OTHER);
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        return new Role.Builder()
                .idRole(rs.getString("id_role"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(RoleStatus.fromValue(rs.getString("status")))
                .fromDatabase()
                .build();
    }
}