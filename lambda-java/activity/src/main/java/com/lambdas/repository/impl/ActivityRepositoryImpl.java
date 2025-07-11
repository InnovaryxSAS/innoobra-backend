package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.ActivityAlreadyExistsException;
import com.lambdas.exception.ActivityNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.model.Activity;
import com.lambdas.model.ActivityStatus;
import com.lambdas.repository.ActivityRepository;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.exception.ValidationException;

public class ActivityRepositoryImpl implements ActivityRepository {

    private final ConnectionPoolManager poolManager;

    public ActivityRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public ActivityRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public Activity save(Activity activity) {
        if (!existsChapterById(activity.getIdChapter())) {
            throw new ValidationException("Chapter with ID " + activity.getIdChapter() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO activities (id_activity, id_chapter, code, name, description, unit, quantity,
                                     created_at, updated_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (activity.getCreatedAt() == null) {
                activity.setCreatedAt(LocalDateTime.now());
            }
            if (activity.getUpdatedAt() == null) {
                activity.setUpdatedAt(LocalDateTime.now());
            }
            if (activity.getStatus() == null) {
                activity.setStatus(ActivityStatus.ACTIVE);
            }

            setActivityParameters(stmt, activity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create activity", null);
            }

            return activity;

        } catch (SQLException e) {
            e.printStackTrace();

            if ("23505".equals(e.getSQLState())) {
                if (e.getMessage().contains("code")) {
                    throw new ActivityAlreadyExistsException("Activity with code " + activity.getCode() + " already exists");
                } else {
                    throw new ActivityAlreadyExistsException("Activity with ID " + activity.getIdActivity() + " already exists");
                }
            }
            
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid chapter ID: " + activity.getIdChapter());
            }

            throw new DatabaseException("Error creating activity: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Activity> findById(String idActivity) {
        final String sql = """
                SELECT id_activity, id_chapter, code, name, description, unit, quantity,
                       created_at, updated_at, status
                FROM activities
                WHERE id_activity = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idActivity);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToActivity(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding activity by ID", e);
        }
    }

    @Override
    public Optional<Activity> findByCode(String code) {
        final String sql = """
                SELECT id_activity, id_chapter, code, name, description, unit, quantity,
                       created_at, updated_at, status
                FROM activities
                WHERE code = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToActivity(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding activity by code", e);
        }
    }

    @Override
    public List<Activity> findAll() {
        final String sql = """
                SELECT id_activity, id_chapter, code, name, description, unit, quantity,
                       created_at, updated_at, status
                FROM activities
                ORDER BY created_at DESC
                """;

        List<Activity> activities = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }

            return activities;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving activities", e);
        }
    }

    @Override
    public List<Activity> findByChapter(String idChapter) {
        final String sql = """
                SELECT id_activity, id_chapter, code, name, description, unit, quantity,
                       created_at, updated_at, status
                FROM activities
                WHERE id_chapter = ?
                ORDER BY created_at DESC
                """;

        List<Activity> activities = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idChapter);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
                return activities;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving activities by chapter", e);
        }
    }

    @Override
    public List<Activity> findByStatus(ActivityStatus status) {
        final String sql = """
                SELECT id_activity, id_chapter, code, name, description, unit, quantity,
                       created_at, updated_at, status
                FROM activities
                WHERE status = ?
                ORDER BY created_at DESC
                """;

        List<Activity> activities = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
                return activities;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving activities by status", e);
        }
    }

    @Override
    public Activity update(Activity activity) {
        if (!existsChapterById(activity.getIdChapter())) {
            throw new ValidationException("Chapter with ID " + activity.getIdChapter() + " does not exist");
        }
        
        final String sql = """
                UPDATE activities
                SET id_chapter = ?, code = ?, name = ?, description = ?, unit = ?, quantity = ?,
                    updated_at = ?, status = ?::activity_status
                WHERE id_activity = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            activity.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, activity.getIdChapter());
            stmt.setString(2, activity.getCode());
            stmt.setString(3, activity.getName());
            stmt.setString(4, activity.getDescription());
            stmt.setString(5, activity.getUnit());
            stmt.setDouble(6, activity.getQuantity());
            stmt.setTimestamp(7, Timestamp.valueOf(activity.getUpdatedAt()));
            stmt.setString(8, activity.getStatus().getValue());
            stmt.setString(9, activity.getIdActivity());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ActivityNotFoundException("Activity with ID " + activity.getIdActivity() + " not found");
            }

            return activity;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid chapter ID: " + activity.getIdChapter());
            }
            
            throw new DatabaseException("Error updating activity", e);
        }
    }

    @Override
    public boolean deactivate(String idActivity) {
        final String sql = """
                UPDATE activities
                SET status = CAST(? AS activity_status), updated_at = ?
                WHERE id_activity = ? AND status != CAST(? AS activity_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = ActivityStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idActivity);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(idActivity)) {
                    throw new ActivityNotFoundException("Activity with ID " + idActivity + " not found");
                } else {
                    throw new ActivityNotFoundException("Activity with ID " + idActivity + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating activity: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(String idActivity) {
        final String sql = "SELECT 1 FROM activities WHERE id_activity = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idActivity);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if activity exists", e);
        }
    }

    @Override
    public boolean existsByCode(String code) {
        final String sql = "SELECT 1 FROM activities WHERE code = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if activity exists by code", e);
        }
    }

    @Override
    public boolean updateQuantity(String idActivity, Double quantity) {
        final String sql = """
                UPDATE activities
                SET quantity = ?, updated_at = ?
                WHERE id_activity = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setDouble(1, quantity);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idActivity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ActivityNotFoundException("Activity with ID " + idActivity + " not found");
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating activity quantity: " + e.getMessage(), e);
        }
    }

    public boolean existsChapterById(String idChapter) {
        final String sql = "SELECT 1 FROM chapters WHERE id_chapter = ? LIMIT 1";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idChapter);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error checking if chapter exists", e);
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

    private void setActivityParameters(PreparedStatement stmt, Activity activity) throws SQLException {
        stmt.setString(1, activity.getIdActivity());
        stmt.setString(2, activity.getIdChapter());
        stmt.setString(3, activity.getCode());
        stmt.setString(4, activity.getName());
        stmt.setString(5, activity.getDescription());
        stmt.setString(6, activity.getUnit());
        stmt.setDouble(7, activity.getQuantity());
        stmt.setTimestamp(8, Timestamp.valueOf(activity.getCreatedAt()));
        stmt.setTimestamp(9, Timestamp.valueOf(activity.getUpdatedAt()));
        stmt.setObject(10, activity.getStatus().getValue(), java.sql.Types.OTHER);
    }

    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        return new Activity.Builder()
                .idActivity(rs.getString("id_activity"))
                .idChapter(rs.getString("id_chapter"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .unit(rs.getString("unit"))
                .quantity(rs.getDouble("quantity"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(ActivityStatus.fromValue(rs.getString("status")))
                .fromDatabase()
                .build();
    }
}