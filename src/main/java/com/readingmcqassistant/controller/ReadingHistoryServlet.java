package com.readingmcqassistant.controller;

import com.readingmcqassistant.dao.HistoryDAO;
import com.readingmcqassistant.bean.History;
import com.readingmcqassistant.bean.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/history")
public class ReadingHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<History> histories = new HistoryDAO().getUserHistories(user.getId());
        request.setAttribute("histories", histories);
        request.getRequestDispatcher("history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] ids = request.getParameterValues("selected");
        if (ids != null && ids.length > 0) {
            new HistoryDAO().deleteMultiple(ids);
        }
        response.sendRedirect("history");
    }
}
