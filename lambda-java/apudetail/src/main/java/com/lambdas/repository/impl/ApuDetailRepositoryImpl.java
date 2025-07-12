package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.ApuDetailAlreadyExistsException;
import com.lambdas.exception.ApuDetailNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.ApuDetail;
import com.lambdas.model.ApuDetailStatus;
import com.lambdas.repository.ApuDetailRepository;
import com.lambdas.repository.ConnectionPoolManager;

public class ApuDetailRepositoryImpl implements ApuDetailRepository {

    private final ConnectionPoolManager poolManager;

    public ApuDetailRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public ApuDetailRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public ApuDetail save(ApuDetail apuDetail) {
        if (!existsActivityById(apuDetail.getIdActivity())) {
            throw new ValidationException("Activity with ID " + apuDetail.getIdActivity() + " does not exist");
        }
        
        if (!existsAttributeById(apuDetail.getIdAttribute())) {
            throw new ValidationException("Attribute with ID " + apuDetail.getIdAttribute() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO apu_details (id_apu_detail, id_activity, id_attribute, quantity,
                                       waste_percentage, created_at, updated_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (apuDetail.getCreatedAt() == null) {
                apuDetail.setCreatedAt(LocalDateTime.now());
            }
            if (apuDetail.getUpdatedAt() == null) {
                apuDetail.setUpdatedAt(LocalDateTime.now());
            }
            if (apuDetail.getStatus() == null) {
                apuDetail.setStatus(ApuDetailStatus.ACTIVE);
            }

            setApuDetailParameters(stmt, apuDetail);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create APU detail", null);
            }

            return apuDetail;

        } catch (SQLException e) {
            e.printStackTrace();

            if ("23505".equals(e.getSQLState())) {
                if (e.getMessage().contains("id_apu_detail")) {
                    throw new ApuDetailAlreadyExistsException("APU detail with ID " + apuDetail.getIdApuDetail() + " already exists");
                } else if (e.getMessage().contains("activity_attribute_unique")) {
                    throw new ApuDetailAlreadyExistsException("APU detail with activity ID " + apuDetail.getIdActivity() + 
                            " and attribute ID " + apuDetail.getIdAttribute() + " already exists");
                }
            }
            
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("id_activity")) {
                    throw new ValidationException("Invalid activity ID: " + apuDetail.getIdActivity());
                } else if (e.getMessage().contains("id_attribute")) {
                    throw new ValidationException("Invalid attribute ID: " + apuDetail.getIdAttribute());
                }
            }

            throw new DatabaseException("Error creating APU detail: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ApuDetail> findById(String idApuDetail) {
        final String sql = """
                SELECT id_apu_detail, id_activity, id_attribute, quantity, waste_percentage,
                       created_at, updated_at, status
                FROM apu_details
                WHERE id_apu_detail = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idApuDetail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToApuDetail(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding APU detail by ID", e);
        }
    }

    @Override
    public List<ApuDetail> findAll() {
        final String sql = """
                SELECT id_apu_detail, id_activity, id_attribute, quantity, waste_percentage,
                       created_at, updated_at, status
                FROM apu_details
                ORDER BY created_at DESC
                """;

        List<ApuDetail> apuDetails = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                apuDetails.add(mapResultSetToApuDetail(rs));
            }

            return apuDetails;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving APU details", e);
        }
    }

    @Override
    public List<ApuDetail> findByActivityId(String idActivity) {
        final String sql = """
                SELECT id_apu_detail, id_activity, id_attribute, quantity, waste_percentage,
                       created_at, updated_at, status
                FROM apu_details
                WHERE id_activity = ?
                ORDER BY created_at DESC
                """;

        List<ApuDetail> apuDetails = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idActivity);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apuDetails.add(mapResultSetToApuDetail(rs));
                }
                return apuDetails;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving APU details by activity ID", e);
        }
    }

    @Override
    public List<ApuDetail> findByAttributeId(String idAttribute) {
        final String sql = """
                SELECT id_apu_detail, id_activity, id_attribute, quantity, waste_percentage,
                       created_at, updated_at, status
                FROM apu_details
                WHERE id_attribute = ?
                ORDER BY created_at DESC
                """;

        List<ApuDetail> apuDetails = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idAttribute);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apuDetails.add(mapResultSetToApuDetail(rs));
                }
                return apuDetails;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving APU details by attribute ID", e);
        }
    }

    @Override
    public List<ApuDetail> findByStatus(ApuDetailStatus status) {
        final String sql = """
                SELECT id_apu_detail, id_activity, id_attribute, quantity, waste_percentage,
                       created_at, updated_at, status
                FROM apu_details
                WHERE status = ?
                ORDER BY created_at DESC
                """;

        List<ApuDetail> apuDetails = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apuDetails.add(mapResultSetToApuDetail(rs));
                }
                return apuDetails;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving APU details by status", e);
        }
    }

    @Override
    public ApuDetail update(ApuDetail apuDetail) {
        if (!existsActivityById(apuDetail.getIdActivity())) {
            throw new ValidationException("Activity with ID " + apuDetail.getIdActivity() + " does not exist");
        }
        
        if (!existsAttributeById(apuDetail.getIdAttribute())) {
            throw new ValidationException("Attribute with ID " + apuDetail.getIdAttribute() + " does not exist");
        }
        
        final String sql = """
                UPDATE apu_details
                SET id_activity = ?, id_attribute = ?, quantity = ?, waste_percentage = ?,
                    updated_at = ?, status = ?::apu_detail_status
                WHERE id_apu_detail = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            apuDetail.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, apuDetail.getIdActivity());
            stmt.setString(2, apuDetail.getIdAttribute());
            stmt.setDouble(3, apuDetail.getQuantity());
            stmt.setDouble(4, apuDetail.getWastePercentage());
            stmt.setTimestamp(5, Timestamp.valueOf(apuDetail.getUpdatedAt()));
            stmt.setString(6, apuDetail.getStatus().getValue());
            stmt.setString(7, apuDetail.getIdApuDetail());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ApuDetailNotFoundException("APU detail with ID " + apuDetail.getIdApuDetail() + " not found");
            }

            return apuDetail;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                if (e.getMessage().contains("id_activity")) {
                    throw new ValidationException("Invalid activity ID: " + apuDetail.getIdActivity());
                } else if (e.getMessage().contains("id_attribute")) {
                    throw new ValidationException("Invalid attribute ID: " + apuDetail.getIdAttribute());
                }
            }
            
            if ("23505".equals(e.getSQLState()) && e.getMessage().contains("activity_attribute_unique")) {
                throw new ApuDetailAlreadyExistsException("APU detail with activity ID " + apuDetail.getIdActivity() + 
                        " and attribute ID " + apuDetail.getIdAttribute() + " already exists");
            }
            
            throw new DatabaseException("Error updating APU detail", e);
        }
    }

    @Override
    public boolean deactivate(String idApuDetail) {
        final String sql = """
                UPDATE apu_details
                SET status = CAST(? AS apu_detail_status), updated_at = ?
                WHERE id_apu_detail = ? AND status != CAST(? AS apu_detail_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = ApuDetailStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idApuDetail);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(idApuDetail)) {
                    throw new ApuDetailNotFoundException("APU detail with ID " + idApuDetail + " not found");
                } else {
                    throw new ApuDetailNotFoundException("APU detail with ID " + idApuDetail + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating APU detail: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String idApuDetail) {
        final String sql = "DELETE FROM apu_details WHERE id_apu_detail = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idApuDetail);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ApuDetailNotFoundException("APU detail with ID " + idApuDetail + " not found");
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deleting APU detail: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(String idApuDetail) {
        final String sql = "SELECT 1 FROM apu_details WHERE id_apu_detail = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idApuDetail);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if APU detail exists", e);
        }
    }

    public boolean existsActivityById(String idActivity) {
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

    public boolean existsAttributeById(String idAttribute) {
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
    public String getConnectionPoolStats() {
        return poolManager.getPoolStats();
    }

    @Override
    public boolean isHealthy() {
        return poolManager.isHealthy();
    }

    private void setApuDetailParameters(PreparedStatement stmt, ApuDetail apuDetail) throws SQLException {
        stmt.setString(1, apuDetail.getIdApuDetail());
        stmt.setString(2, apuDetail.getIdActivity());
        stmt.setString(3, apuDetail.getIdAttribute());
        stmt.setDouble(4, apuDetail.getQuantity());
        stmt.setDouble(5, apuDetail.getWastePercentage());
        stmt.setTimestamp(6, Timestamp.valueOf(apuDetail.getCreatedAt()));
        stmt.setTimestamp(7, Timestamp.valueOf(apuDetail.getUpdatedAt()));
        stmt.setObject(8, apuDetail.getStatus().getValue(), java.sql.Types.OTHER);
    }

    private ApuDetail mapResultSetToApuDetail(ResultSet rs) throws SQLException {
        return new ApuDetail.Builder()
                .idApuDetail(rs.getString("id_apu_detail"))
                .idActivity(rs.getString("id_activity"))
                .idAttribute(rs.getString("id_attribute"))
                .quantity(rs.getDouble("quantity"))
                .wastePercentage(rs.getDouble("waste_percentage"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(ApuDetailStatus.fromValue(rs.getString("status")))
                .fromDatabase()
                .build();
    }
}