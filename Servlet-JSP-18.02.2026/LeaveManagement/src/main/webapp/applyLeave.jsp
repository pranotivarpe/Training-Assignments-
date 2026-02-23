<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ page import="com.example.leavemanagement.model.User"
%> <% User user = (User) session.getAttribute("user"); if(user == null) {
response.sendRedirect("login.jsp"); return; } %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Apply for Leave</title>
  </head>
  <body>
    <h2>Welcome, <%= user.getUsername() %>!</h2>
    <h3>Apply for Leave</h3>

    <% if(request.getAttribute("message") != null) { %>
    <p style="color: green;"><%= request.getAttribute("message") %></p>
    <% } %> <% if(request.getAttribute("error") != null) { %>
    <p style="color: red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="applyLeave" method="post">
      Start Date: <input type="date" name="startDate" required /><br />
      End Date: <input type="date" name="endDate" required /><br />
      Reason: <textarea name="reason" rows="4" cols="50" required></textarea
      ><br />
      <input type="submit" value="Apply" />
    </form>
    <br />
    <a href="logout">Logout</a>
  </body>
</html>
