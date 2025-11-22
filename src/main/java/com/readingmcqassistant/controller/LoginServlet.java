package com.readingmcqassistant.controller;

import com.readingmcqassistant.dao.UserDAO;
import com.readingmcqassistant.bean.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            response.sendRedirect("home.jsp"); // lát nữa tạo trang này
        } else {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
