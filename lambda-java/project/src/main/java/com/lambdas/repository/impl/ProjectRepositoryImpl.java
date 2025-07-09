package com.lambdas.repository.impl;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.ProjectAlreadyExistsException;
import com.lambdas.exception.ProjectNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.CompanyNotFoundException;
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
        final String sql = """
                INSERT INTO projects (id, name, description, address, city, state, country,
                                    created_at, updated_at, status, responsible_user, data_source,
                                    company, created_by, budget, inventory)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            validateCompanyExists(project.getCompany());

            if (project.getCreatedAt() == null) {
                project.setCreatedAt(LocalDateTime.now());
            }
            if (project.getUpdatedAt() == null) {
                project.setUpdatedAt(LocalDateTime.now());
            }
            if (project.getStatus() == null) {
                project.setStatus(ProjectStatus.ACTIVE);
            }

            setProjectParameters(stmt, project);

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
                throw new CompanyNotFoundException("Company with ID " + project.getCompany() + " not found");
            }

            throw new DatabaseException("Error creating project: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Project> findById(String id) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source,
                       company, created_by, budget, inventory
                FROM projects
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

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
                       created_at, updated_at, status, responsible_user, data_source,
                       company, created_by, budget, inventory
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
                       created_at, updated_at, status, responsible_user, data_source,
                       company, created_by, budget, inventory
                FROM projects
                WHERE status = ?
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
    public List<Project> findByCompany(String companyId) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source,
                       company, created_by, budget, inventory
                FROM projects
                WHERE company = ?
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyId);

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
    public List<Project> findByResponsibleUser(String userId) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source,
                       company, created_by, budget, inventory
                FROM projects
                WHERE responsible_user = ?
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

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
    public List<Project> findByCompanyAndStatus(String companyId, ProjectStatus status) {
        final String sql = """
                SELECT id, name, description, address, city, state, country,
                       created_at, updated_at, status, responsible_user, data_source,
                       company, created_by, budget, inventory
                FROM projects
                WHERE company = ? AND status = ?
                ORDER BY created_at DESC
                """;

        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyId);
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
        final String sql = """
                UPDATE projects
                SET name = ?, description = ?, address = ?, city = ?, state = ?, country = ?,
                    updated_at = ?, status = ?::project_status, responsible_user = ?, data_source = ?,
                    company = ?, created_by = ?, budget = ?, inventory = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            validateCompanyExists(project.getCompany());

            project.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getAddress());
            stmt.setString(4, project.getCity());
            stmt.setString(5, project.getState());
            stmt.setString(6, project.getCountry());
            stmt.setTimestamp(7, Timestamp.valueOf(project.getUpdatedAt()));
            stmt.setString(8, project.getStatus().getValue());
            stmt.setString(9, project.getResponsibleUser());
            stmt.setString(10, project.getDataSource());
            stmt.setString(11, project.getCompany());
            stmt.setString(12, project.getCreatedBy());
            stmt.setBigDecimal(13, project.getBudget());
            stmt.setString(14, project.getInventory());
            stmt.setString(15, project.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ProjectNotFoundException("Project with ID " + project.getId() + " not found");
            }

            return project;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new CompanyNotFoundException("Company with ID " + project.getCompany() + " not found");
            }
            throw new DatabaseException("Error updating project", e);
        }
    }

    @Override
    public boolean deactivate(String id) {
        final String sql = """
                UPDATE projects
                SET status = CAST(? AS project_status), updated_at = ?
                WHERE id = ? AND status != CAST(? AS project_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = ProjectStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, id);
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
    public boolean existsById(String id) {
        final String sql = "SELECT 1 FROM projects WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if project exists", e);
        }
    }

    private void validateCompanyExists(String companyId) {
        final String sql = "SELECT 1 FROM companies WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new CompanyNotFoundException("Company with ID " + companyId + " not found");
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error validating company existence", e);
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

    private void setProjectParameters(PreparedStatement stmt, Project project) throws SQLException {
        stmt.setString(1, project.getId());
        stmt.setString(2, project.getName());
        stmt.setString(3, project.getDescription());
        stmt.setString(4, project.getAddress());
        stmt.setString(5, project.getCity());
        stmt.setString(6, project.getState());
        stmt.setString(7, project.getCountry());
        stmt.setTimestamp(8, Timestamp.valueOf(project.getCreatedAt()));
        stmt.setTimestamp(9, Timestamp.valueOf(project.getUpdatedAt()));
        stmt.setObject(10, project.getStatus().getValue(), java.sql.Types.OTHER);
        stmt.setString(11, project.getResponsibleUser());
        stmt.setString(12, project.getDataSource());
        stmt.setString(13, project.getCompany());
        stmt.setString(14, project.getCreatedBy());
        stmt.setBigDecimal(15, project.getBudget());
        stmt.setString(16, project.getInventory());
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        return new Project.Builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .address(rs.getString("address"))
                .city(rs.getString("city"))
                .state(rs.getString("state"))
                .country(rs.getString("country"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(ProjectStatus.fromValue(rs.getString("status")))
                .responsibleUser(rs.getString("responsible_user"))
                .dataSource(rs.getString("data_source"))
                .company(rs.getString("company"))
                .createdBy(rs.getString("created_by"))
                .budget(rs.getBigDecimal("budget"))
                .inventory(rs.getString("inventory"))
                .fromDatabase()
                .build();
    }
}