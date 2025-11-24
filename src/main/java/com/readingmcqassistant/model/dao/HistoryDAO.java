package com.readingmcqassistant.model.dao;

import com.readingmcqassistant.model.bean.History;

import java.sql.*;
import java.util.*;

public class HistoryDAO {

    public void addHistory(History h) {
    	String sql = """
    			  INSERT INTO histories
    			  (user_id, passage, question, option_a, option_b, option_c, option_d, answer_letter)
    			  VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    			""";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, h.getUserId());
            stmt.setString(2, h.getPassage());
            stmt.setString(3, h.getQuestion());
            stmt.setString(4, h.getOptionA());
            stmt.setString(5, h.getOptionB());
            stmt.setString(6, h.getOptionC());
            stmt.setString(7, h.getOptionD());
            stmt.setString(8, h.getCorrectAnswer());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<History> getUserHistories(int userId) {
        List<History> list = new ArrayList<>();
        String sql = "SELECT * FROM histories WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                History h = new History();
                h.setId(rs.getInt("id"));
                h.setUserId(rs.getInt("user_id"));
                h.setPassage(rs.getString("passage"));
                h.setQuestion(rs.getString("question"));
                h.setOptionA(rs.getString("option_a"));
                h.setOptionB(rs.getString("option_b"));
                h.setOptionC(rs.getString("option_c"));
                h.setOptionD(rs.getString("option_d"));
                h.setCorrectAnswer(rs.getString("answer_letter"));
                h.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteHistory(int id) {
        String sql = "DELETE FROM histories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMultiple(String[] ids) {
        String sql = "DELETE FROM histories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String id : ids) {
                stmt.setInt(1, Integer.parseInt(id));
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String findAnswerLetter(long historyId) {
    	  String sql = "SELECT answer_letter FROM histories WHERE id = ?";
    	  try (Connection c = DBConnection.getConnection();
    	       PreparedStatement ps = c.prepareStatement(sql)) {
    	    ps.setLong(1, historyId);
    	    try (ResultSet rs = ps.executeQuery()) {
    	      if (rs.next()) return rs.getString(1);
    	    }
    	  } catch (SQLException e) { e.printStackTrace(); }
    	  return null;
    	}

}
