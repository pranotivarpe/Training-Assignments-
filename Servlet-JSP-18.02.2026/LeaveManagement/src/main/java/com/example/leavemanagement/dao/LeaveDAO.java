package com.example.leavemanagement.dao;

import com.example.leavemanagement.model.Leave;
import com.example.leavemanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    // Apply for leave (insert)
    public boolean applyLeave(Leave leave) {
        String sql = "INSERT INTO leaves (user_id, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, 'pending')";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, leave.getUserId());
            ps.setDate(2, leave.getStartDate());
            ps.setDate(3, leave.getEndDate());
            ps.setString(4, leave.getReason());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all pending leaves (for admin)
    public List<Leave> getPendingLeaves() {
        List<Leave> leaves = new ArrayList<>();
        String sql = "SELECT * FROM leaves WHERE status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Leave leave = new Leave();
                leave.setId(rs.getInt("id"));
                leave.setUserId(rs.getInt("user_id"));
                leave.setStartDate(rs.getDate("start_date"));
                leave.setEndDate(rs.getDate("end_date"));
                leave.setReason(rs.getString("reason"));
                leave.setStatus(rs.getString("status"));
                leaves.add(leave);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaves;
    }

    // Update leave status (approve/reject)
    public boolean updateLeaveStatus(int leaveId, String status) {
        String sql = "UPDATE leaves SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, leaveId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}