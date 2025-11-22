<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.readingmcqassistant.bean.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f9;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }

        .home-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            width: 360px;
            text-align: center;
        }

        h2 {
            color: #333;
            margin-bottom: 20px;
        }

        .btn {
            display: inline-block;
            margin: 8px 0;
            background-color: #4285f4;
            color: white;
            padding: 10px 20px;
            border-radius: 4px;
            text-decoration: none;
        }

        .btn:hover {
            background-color: #3367d6;
        }

        .logout {
            background-color: #d9534f;
        }

        .logout:hover {
            background-color: #c9302c;
        }
    </style>
</head>
<body>
    <div class="home-container">
        <h2>Welcome, <%= user.getUsername() %>!</h2>
        <p>You are successfully logged in.</p>

        <a href="reading.jsp" class="btn">Go to Reading MCQ</a><br>
        <a href="logout" class="btn logout">Logout</a>
    </div>
</body>
</html>
