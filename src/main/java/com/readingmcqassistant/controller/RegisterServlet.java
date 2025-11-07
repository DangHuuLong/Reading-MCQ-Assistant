package com.readingmcqassistant.controller;

import com.readingmcqassistant.dao.UserDAO;
import com.readingmcqassistant.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = new User(username, password);
        UserDAO dao = new UserDAO();

        boolean success = dao.register(user);

        if (success) {
            response.sendRedirect("login.jsp");
        } else {
            response.getWriter().println("<h3>Registration failed. Username might already exist.</h3>");
        }
    }
}
