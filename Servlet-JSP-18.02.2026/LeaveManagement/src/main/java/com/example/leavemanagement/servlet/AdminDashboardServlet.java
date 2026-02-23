package com.example.leavemanagement.servlet;

import com.example.leavemanagement.dao.LeaveDAO;
import com.example.leavemanagement.model.Leave;
import com.example.leavemanagement.model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/adminDashboard")
public class AdminDashboardServlet extends HttpServlet {
    private LeaveDAO leaveDAO = new LeaveDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        // Check if user is admin
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Leave> pendingLeaves = leaveDAO.getPendingLeaves();
        request.setAttribute("leaves", pendingLeaves);
        request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int leaveId = Integer.parseInt(request.getParameter("leaveId"));

        if ("approve".equals(action)) {
            leaveDAO.updateLeaveStatus(leaveId, "approved");
        } else if ("reject".equals(action)) {
            leaveDAO.updateLeaveStatus(leaveId, "rejected");
        }

        response.sendRedirect("adminDashboard");
    }
}