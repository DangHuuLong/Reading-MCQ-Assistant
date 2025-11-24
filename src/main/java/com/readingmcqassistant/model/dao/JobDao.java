package com.readingmcqassistant.model.dao;

import java.sql.*;

public class JobDao {

  public long enqueue(long userId, String payloadJson) {
    // Lưu chuỗi JSON thẳng vào LONGTEXT, không CAST
    String sql = """
      INSERT INTO jobs (user_id, payload_json, status, attempts, max_attempts, created_at, updated_at)
      VALUES (?, ?, 'QUEUED', 0, 3, NOW(), NOW())
      """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setLong(1, userId);
      ps.setString(2, payloadJson); // payload JSON string
      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) return rs.getLong(1);
      }
      throw new RuntimeException("enqueue failed: no generated key returned");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("enqueue failed: " + e.getMessage());
    }
  }

  public JobStatus findStatus(long jobId, long userId) {
    String sql = "SELECT status, history_id, last_error FROM jobs WHERE id=? AND user_id=?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, jobId);
      ps.setLong(2, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          JobStatus s = new JobStatus();
          s.status = rs.getString("status");

          long hid = rs.getLong("history_id");
          s.historyId = rs.wasNull() ? null : hid;

          s.error = rs.getString("last_error");
          return s;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static class JobStatus {
    public String status;
    public Long historyId; // có thể null
    public String error;
  }
}
