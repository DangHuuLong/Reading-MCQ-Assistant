package com.readingmcqassistant.service;

import com.readingmcqassistant.utils.DBConnection;

import java.sql.*;

public class JobWorker implements Runnable {
  private volatile boolean running = true;
  private final String workerName;

  private final AIClient ai = new AIClient();
  public JobWorker(String workerName) {
      this.workerName = workerName;
  }
  public void shutdown() { running = false; }

  @Override public void run() {
    while (running) {
      boolean didWork = processOne();
      try { Thread.sleep(didWork ? 50 : 500); } catch (InterruptedException ignored) {}
    }
  }

  private boolean processOne() {
    try (Connection c = DBConnection.getConnection()) {
      c.setAutoCommit(false);

      // 1) Lease 1 job
      long jobId = -1, userId = -1;
      String payload = null;

      try (PreparedStatement ps = c.prepareStatement("""
    		    SELECT id, user_id, payload_json AS payload
    		    FROM jobs
    		    WHERE status='QUEUED'
    		    ORDER BY created_at
    		    LIMIT 1
    		    FOR UPDATE
    		""");
    		     ResultSet rs = ps.executeQuery()) {

    		  if (!rs.next()) { c.rollback(); return false; }
    		  jobId  = rs.getLong("id");
    		  userId = rs.getLong("user_id");
    		  payload = rs.getString("payload");   // chính là payload_json
    		}
      System.out.println("JOB " + jobId + " PAYLOAD = " + payload);


      try (PreparedStatement upd = c.prepareStatement("""
          UPDATE jobs
          SET status='LEASED', lease_owner=?, lease_until=DATE_ADD(NOW(), INTERVAL 30 SECOND),
              attempts=attempts+1, updated_at=NOW()
          WHERE id=?
      """)) {
        upd.setString(1, workerName);
        upd.setLong(2, jobId);
        upd.executeUpdate();
      }
      c.commit();

      // 2) GỌI AI THẬT
      String answer;
      Integer latency;
      try {
        AIClient.Result r = ai.predictRawPayload(payload);
        answer  = r.answerLetter;          // "A"|"B"|"C"|"D"
        latency = r.latencyMs;             // có thể null
      } catch (Exception e) {
        // Lỗi: cho retry nếu còn attempts < max_attempts; hết thì FAILED
        try (Connection c2 = DBConnection.getConnection();
             PreparedStatement ps = c2.prepareStatement("""
               UPDATE jobs
               SET status = CASE WHEN attempts < max_attempts THEN 'QUEUED' ELSE 'FAILED' END,
                   last_error = ?, updated_at = NOW()
               WHERE id = ?
             """)) {
          ps.setString(1, e.toString());
          ps.setLong(2, jobId);
          ps.executeUpdate();
        }
        return true; // đã xử lý 1 job
      }

      // 3) Lưu lịch sử
      long historyId = insertHistoryFromPayload(userId, payload, answer, latency);

      // 4) Đánh dấu thành công
      try (PreparedStatement ps = c.prepareStatement("""
          UPDATE jobs
          SET status='SUCCEEDED', history_id=?, last_error=NULL, updated_at=NOW()
          WHERE id=?
      """)) {
        ps.setLong(1, historyId);
        ps.setLong(2, jobId);
        ps.executeUpdate();
        c.commit();  // ✅
        System.out.println("[JobWorker] ✅ Job " + jobId + " SUCCEEDED, answer=" + answer);
      }
      return true;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private long insertHistoryFromPayload(long userId, String payloadJson, String answer, Integer latency) throws SQLException {
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement("""
            INSERT INTO histories
              (user_id, passage, question, option_a, option_b, option_c, option_d, answer_letter, latency_ms)
            VALUES
              (?, JSON_UNQUOTE(JSON_EXTRACT(?, '$.passage')),
                 JSON_UNQUOTE(JSON_EXTRACT(?, '$.question')),
                 JSON_UNQUOTE(JSON_EXTRACT(?, '$.options[0]')),
                 JSON_UNQUOTE(JSON_EXTRACT(?, '$.options[1]')),
                 JSON_UNQUOTE(JSON_EXTRACT(?, '$.options[2]')),
                 JSON_UNQUOTE(JSON_EXTRACT(?, '$.options[3]')),
                 ?, ?)
         """, Statement.RETURN_GENERATED_KEYS)) {
      ps.setLong(1, userId);
      for (int i = 2; i <= 7; i++) ps.setString(i, payloadJson);
      ps.setString(8, answer);
      if (latency == null) ps.setNull(9, Types.INTEGER); else ps.setInt(9, latency);
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) return rs.getLong(1);
      }
    }
    throw new SQLException("insertHistory failed");
  }
}
