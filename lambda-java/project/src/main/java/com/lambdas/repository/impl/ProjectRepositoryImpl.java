package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.exception.ProjectAlreadyExistsException;
import com.lambdas.exception.ProjectNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.repository.ProjectRepository;

public class ProjectRepositoryImpl implements ProjectRepository {

    private final ConnectionPoolManager poolManager;

    public ProjectRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public ProjectRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public Project save(Project project) {
        if (project.getCompanyId() != null && !existsCompanyById(project.getCompanyId())) {
            throw new ValidationException("Company ID " + project.getCompanyId() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO projects (id, name, description, address, city, state, country,
                                    created_at, updated_at, status, responsible_user, data_source_id,
                                    company_id, created_by, budget_amount)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::status_enum, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (project.getCreatedAt() == null) {
                project.setCreatedAt(LocalDateTime.now());
            }
            if (project.getUpdatedAt() == null) {
                project.setUpdatedAt(LocalDateTime.now());
            }
            if (project.getStatus() == null) {
                project.setStatus(ProjectStatus.ACTIVE);
            }

            stmt.setObject(1, project.getId());
            stmt.setString(2, project.getName());
            stmt.setString(3, project.getDescription());
            stmt.setString(4, project.getAddress());
            stmt.setString(5, project.getCity());
            stmt.setString(6, project.getState());
            stmt.setString(7, project.getCountry());
            stmt.setTimestamp(8, Timestamp.valueOf(project.getCreatedAt()));
            stmt.setTimestamp(9, Timestamp.valueOf(project.getUpdatedAt()));
            stmt.setString(10, project.getStatus().getValue());
            stmt.setObject(11, project.getResponsibleUser());
            stmt.setObject(12, project.getDataSourceId());
            stmt.setObject(13, project.getCompanyId());
            stmt.setObject(14, project.getCreatedBy());
            stmt.setBigDecimal(15, project.getBudgetAmount());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create project", null);
            }

            return project;

        } catch (SQLException e) {
            e.printStackTrace();

            if ("23505".equals(e.getSQLState())) {
                throw new ProjectAlreadyExistsException("Project with ID " + project.getId() + " already exists");
            }
            
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("company_id")) {
                    throw new ValidationException("Invalid company ID: " + project.getCompanyId());
                }
            }

            throw new DatabaseException("Error creating project: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Project> findById(UUID id) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source_id,
                       company_id, created_by, budget_amount
                FROM projects
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProject(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding project by ID", e);
        }
    }

    @Override
    public List<Project> findAll() {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source_id,
                       company_id, created_by, budget_amount
                FROM projects
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }

            return projects;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving projects", e);
        }
    }

    @Override
    public List<Project> findByStatus(ProjectStatus status) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source_id,
                       company_id, created_by, budget_amount
                FROM projects
                WHERE status = ?::status_enum
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }

            return projects;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving projects by status", e);
        }
    }

    @Override
    public List<Project> findByCompany(UUID companyId) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source_id,
                       company_id, created_by, budget_amount
                FROM projects
                WHERE company_id = ?
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, companyId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }

            return projects;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving projects by company", e);
        }
    }

    @Override
    public List<Project> findByResponsibleUser(UUID userId) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source_id,
                       company_id, created_by, budget_amount
                FROM projects
                WHERE responsible_user = ?
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }

            return projects;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving projects by responsible user", e);
        }
    }

    @Override
    public List<Project> findByCompanyAndStatus(UUID companyId, ProjectStatus status) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source_id,
                       company_id, created_by, budget_amount
                FROM projects
                WHERE company_id = ? AND status = ?::status_enum
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, companyId);
            stmt.setString(2, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }

            return projects;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving projects by company and status", e);
        }
    }

    @Override
    public Project update(Project project) {
        if (project.getCompanyId() != null && !existsCompanyById(project.getCompanyId())) {
            throw new ValidationException("Company ID " + project.getCompanyId() + " does not exist");
        }
        
        final String sql = """
                UPDATE projects
                SET name = ?, description = ?, address = ?, city = ?, state = ?, country = ?,
                    updated_at = ?, status = ?::status_enum, responsible_user = ?, data_source_id = ?,
                    company_id = ?, created_by = ?, budget_amount = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            project.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getAddress());
            stmt.setString(4, project.getCity());
            stmt.setString(5, project.getState());
            stmt.setString(6, project.getCountry());
            stmt.setTimestamp(7, Timestamp.valueOf(project.getUpdatedAt()));
            stmt.setString(8, project.getStatus().getValue());
            stmt.setObject(9, project.getResponsibleUser());
            stmt.setObject(10, project.getDataSourceId());
            stmt.setObject(11, project.getCompanyId());
            stmt.setObject(12, project.getCreatedBy());
            stmt.setBigDecimal(13, project.getBudgetAmount());
            stmt.setObject(14, project.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ProjectNotFoundException("Project with ID " + project.getId() + " not found");
            }

            return project;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("company_id")) {
                    throw new ValidationException("Invalid company ID: " + project.getCompanyId());
                }
            }
            
            throw new DatabaseException("Error updating project", e);
        }
    }

    @Override
    public boolean deactivate(UUID id) {
        final String sql = """
                UPDATE projects
                SET status = ?::status_enum, updated_at = ?
                WHERE id = ? AND status != ?::status_enum
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = ProjectStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setObject(3, id);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(id)) {
                    throw new ProjectNotFoundException("Project with ID " + id + " not found");
                } else {
                    throw new ProjectNotFoundException("Project with ID " + id + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating project: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        final String sql = "SELECT 1 FROM projects WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if project exists", e);
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

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID companyId = (UUID) rs.getObject("company_id");
        UUID responsibleUser = (UUID) rs.getObject("responsible_user");
        UUID dataSourceId = (UUID) rs.getObject("data_source_id");
        UUID createdBy = (UUID) rs.getObject("created_by");
        
        return new Project.Builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .address(rs.getString("address"))
                .city(rs.getString("city"))
                .state(rs.getString("state"))
                .country(rs.getString("country"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(ProjectStatus.fromValue(rs.getString("status")))
                .responsibleUser(responsibleUser)
                .dataSourceId(dataSourceId)
                .companyId(companyId)
                .createdBy(createdBy)
                .budgetAmount(rs.getBigDecimal("budget_amount"))
                .fromDatabase()
                .build();
    }
}