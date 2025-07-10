package com.lambdas.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lambdas.exception.ChapterAlreadyExistsException;
import com.lambdas.exception.ChapterNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.Chapter;
import com.lambdas.model.ChapterStatus;
import com.lambdas.repository.ChapterRepository;
import com.lambdas.repository.ConnectionPoolManager;

public class ChapterRepositoryImpl implements ChapterRepository {

    private final ConnectionPoolManager poolManager;

    public ChapterRepositoryImpl() {
        this.poolManager = ConnectionPoolManager.getInstance();
    }

    public ChapterRepositoryImpl(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private Connection getConnection() throws SQLException {
        return poolManager.getConnection();
    }

    @Override
    public Chapter save(Chapter chapter) {
        if (!existsBudgetById(chapter.getIdBudget())) {
            throw new ValidationException("Budget with ID " + chapter.getIdBudget() + " does not exist");
        }
        
        final String sql = """
                INSERT INTO chapters (id_chapter, id_budget, code, name, description,
                                    created_at, updated_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (chapter.getCreatedAt() == null) {
                chapter.setCreatedAt(LocalDateTime.now());
            }
            if (chapter.getUpdatedAt() == null) {
                chapter.setUpdatedAt(LocalDateTime.now());
            }
            if (chapter.getStatus() == null) {
                chapter.setStatus(ChapterStatus.ACTIVE);
            }

            setChapterParameters(stmt, chapter);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create chapter", null);
            }

            return chapter;

        } catch (SQLException e) {
            e.printStackTrace();

            if ("23505".equals(e.getSQLState())) {
                if (e.getMessage().contains("code")) {
                    throw new ChapterAlreadyExistsException("Chapter with code " + chapter.getCode() + " already exists");
                } else {
                    throw new ChapterAlreadyExistsException("Chapter with ID " + chapter.getIdChapter() + " already exists");
                }
            }
            
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid budget ID: " + chapter.getIdBudget());
            }

            throw new DatabaseException("Error creating chapter: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Chapter> findById(String idChapter) {
        final String sql = """
                SELECT id_chapter, id_budget, code, name, description,
                       created_at, updated_at, status
                FROM chapters
                WHERE id_chapter = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idChapter);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToChapter(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding chapter by ID", e);
        }
    }

    @Override
    public Optional<Chapter> findByCode(String code) {
        final String sql = """
                SELECT id_chapter, id_budget, code, name, description,
                       created_at, updated_at, status
                FROM chapters
                WHERE code = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToChapter(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error finding chapter by code", e);
        }
    }

    @Override
    public List<Chapter> findAll() {
        final String sql = """
                SELECT id_chapter, id_budget, code, name, description,
                       created_at, updated_at, status
                FROM chapters
                ORDER BY created_at DESC
                """;

        List<Chapter> chapters = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                chapters.add(mapResultSetToChapter(rs));
            }

            return chapters;

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving chapters", e);
        }
    }

    @Override
    public List<Chapter> findByBudgetId(String idBudget) {
        final String sql = """
                SELECT id_chapter, id_budget, code, name, description,
                       created_at, updated_at, status
                FROM chapters
                WHERE id_budget = ?
                ORDER BY created_at DESC
                """;

        List<Chapter> chapters = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idBudget);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chapters.add(mapResultSetToChapter(rs));
                }

                return chapters;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving chapters by budget ID", e);
        }
    }

    @Override
    public List<Chapter> findByStatus(ChapterStatus status) {
        final String sql = """
                SELECT id_chapter, id_budget, code, name, description,
                       created_at, updated_at, status
                FROM chapters
                WHERE status = ?
                ORDER BY created_at DESC
                """;

        List<Chapter> chapters = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chapters.add(mapResultSetToChapter(rs));
                }

                return chapters;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving chapters by status", e);
        }
    }

    @Override
    public Chapter update(Chapter chapter) {
        if (!existsBudgetById(chapter.getIdBudget())) {
            throw new ValidationException("Budget with ID " + chapter.getIdBudget() + " does not exist");
        }
        
        final String sql = """
                UPDATE chapters
                SET id_budget = ?, code = ?, name = ?, description = ?,
                    updated_at = ?, status = ?::chapter_status
                WHERE id_chapter = ?
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            chapter.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, chapter.getIdBudget());
            stmt.setString(2, chapter.getCode());
            stmt.setString(3, chapter.getName());
            stmt.setString(4, chapter.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(chapter.getUpdatedAt()));
            stmt.setString(6, chapter.getStatus().getValue());
            stmt.setString(7, chapter.getIdChapter());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ChapterNotFoundException("Chapter with ID " + chapter.getIdChapter() + " not found");
            }

            return chapter;

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new ValidationException("Invalid budget ID: " + chapter.getIdBudget());
            }
            
            if ("23505".equals(e.getSQLState()) && e.getMessage().contains("code")) {
                throw new ChapterAlreadyExistsException("Chapter with code " + chapter.getCode() + " already exists");
            }
            
            throw new DatabaseException("Error updating chapter", e);
        }
    }

    @Override
    public boolean deactivate(String idChapter) {
        final String sql = """
                UPDATE chapters
                SET status = CAST(? AS chapter_status), updated_at = ?
                WHERE id_chapter = ? AND status != CAST(? AS chapter_status)
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String inactiveStatus = ChapterStatus.INACTIVE.getValue();

            stmt.setString(1, inactiveStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, idChapter);
            stmt.setString(4, inactiveStatus);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                if (!existsById(idChapter)) {
                    throw new ChapterNotFoundException("Chapter with ID " + idChapter + " not found");
                } else {
                    throw new ChapterNotFoundException("Chapter with ID " + idChapter + " is already inactive");
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating chapter: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(String idChapter) {
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
    public boolean existsByCode(String code) {
        final String sql = "SELECT 1 FROM chapters WHERE code = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if chapter exists by code", e);
        }
    }

    @Override
    public boolean existsByCodeAndBudgetId(String code, String idBudget) {
        final String sql = "SELECT 1 FROM chapters WHERE code = ? AND id_budget = ? LIMIT 1";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.setString(2, idBudget);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking if chapter exists by code and budget ID", e);
        }
    }

    public boolean existsBudgetById(String idBudget) {
        final String sql = "SELECT 1 FROM budgets WHERE id_budget = ? LIMIT 1";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idBudget);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error checking if budget exists", e);
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

    private void setChapterParameters(PreparedStatement stmt, Chapter chapter) throws SQLException {
        stmt.setString(1, chapter.getIdChapter());
        stmt.setString(2, chapter.getIdBudget());
        stmt.setString(3, chapter.getCode());
        stmt.setString(4, chapter.getName());
        stmt.setString(5, chapter.getDescription());
        stmt.setTimestamp(6, Timestamp.valueOf(chapter.getCreatedAt()));
        stmt.setTimestamp(7, Timestamp.valueOf(chapter.getUpdatedAt()));
        stmt.setObject(8, chapter.getStatus().getValue(), java.sql.Types.OTHER);
    }

    private Chapter mapResultSetToChapter(ResultSet rs) throws SQLException {
        return new Chapter.Builder()
                .idChapter(rs.getString("id_chapter"))
                .idBudget(rs.getString("id_budget"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .status(ChapterStatus.fromValue(rs.getString("status")))
                .fromDatabase()
                .build();
    }
}