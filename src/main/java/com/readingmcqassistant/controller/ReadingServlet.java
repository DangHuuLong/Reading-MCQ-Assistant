package com.readingmcqassistant.controller;

import com.readingmcqassistant.bo.ReadingBO;
import com.readingmcqassistant.bean.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reading")
public class ReadingServlet extends HttpServlet {

    private final ReadingBO readingBO = new ReadingBO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        String passage = request.getParameter("passage");
        String question = request.getParameter("question");
        String a = request.getParameter("optionA");
        String b = request.getParameter("optionB");
        String c = request.getParameter("optionC");
        String d = request.getParameter("optionD");

        long jobId = readingBO.submitJob(
                user.getId(), passage, question, a, b, c, d);

        if ("true".equals(request.getHeader("X-Test"))) {
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"jobId\":" + jobId + "}");
            return;
        }

        request.setAttribute("jobId", jobId);
        request.getRequestDispatcher("/reading_pending.jsp").forward(request, response);

    }
}
