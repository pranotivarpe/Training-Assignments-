package com.example.leavemanagement.servlet;

import com.example.leavemanagement.dao.LeaveDAO;
import com.example.leavemanagement.model.Leave;
import com.example.leavemanagement.model.User;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/applyLeave")
public class ApplyLeaveServlet extends HttpServlet {
    private LeaveDAO leaveDAO = new LeaveDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String start = request.getParameter("startDate");
        String end = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        Leave leave = new Leave();
        leave.setUserId(user.getId());
        leave.setStartDate(Date.valueOf(start)); // expects yyyy-mm-dd
        leave.setEndDate(Date.valueOf(end));
        leave.setReason(reason);

        boolean success = leaveDAO.applyLeave(leave);

        if (success) {
            request.setAttribute("message", "Leave applied successfully!");
        } else {
            request.setAttribute("error", "Failed to apply leave.");
        }
        request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
    }
}