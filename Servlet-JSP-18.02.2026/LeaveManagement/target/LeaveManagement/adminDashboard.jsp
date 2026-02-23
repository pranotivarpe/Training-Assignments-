<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ page import="java.util.List,
com.example.leavemanagement.model.Leave, com.example.leavemanagement.model.User"
%> <% User user = (User) session.getAttribute("user"); if(user == null ||
!"admin".equals(user.getRole())) { response.sendRedirect("login.jsp"); return; }
List<Leave>
  leaves = (List<Leave
    >) request.getAttribute("leaves"); %>
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8" />
        <title>Admin Dashboard</title>
      </head>
      <body>
        <h2>Admin Dashboard</h2>
        <h3>Pending Leave Requests</h3>

        <table border="1">
          <tr>
            <th>Leave ID</th>
            <th>User ID</th>
            <th>Start Date</th>
            <th>End Date</th>
            <th>Reason</th>
            <th>Action</th>
          </tr>
          <% if(leaves != null && !leaves.isEmpty()) { for(Leave leave : leaves)
          { %>
          <tr>
            <td><%= leave.getId() %></td>
            <td><%= leave.getUserId() %></td>
            <td><%= leave.getStartDate() %></td>
            <td><%= leave.getEndDate() %></td>
            <td><%= leave.getReason() %></td>
            <td>
              <form
                action="adminDashboard"
                method="post"
                style="display: inline;"
              >
                <input
                  type="hidden"
                  name="leaveId"
                  value="<%= leave.getId() %>"
                />
                <input type="hidden" name="action" value="approve" />
                <input type="submit" value="Approve" />
              </form>
              <form
                action="adminDashboard"
                method="post"
                style="display: inline;"
              >
                <input
                  type="hidden"
                  name="leaveId"
                  value="<%= leave.getId() %>"
                />
                <input type="hidden" name="action" value="reject" />
                <input type="submit" value="Reject" />
              </form>
            </td>
          </tr>
          <% } } else { %>
          <tr>
            <td colspan="6">No pending leave requests.</td>
          </tr>
          <% } %>
        </table>
        <br />
        <a href="logout">Logout</a>
      </body>
    </html></Leave
  ></Leave
>
