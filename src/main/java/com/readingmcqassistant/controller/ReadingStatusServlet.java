package com.readingmcqassistant.controller;

import com.readingmcqassistant.model.bo.ReadingStatusBO;
import com.readingmcqassistant.model.bean.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reading/status")
public class ReadingStatusServlet extends HttpServlet {

    private final ReadingStatusBO statusBO = new ReadingStatusBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendError(401);
            return;
        }
        User user = (User) session.getAttribute("user");

        long jobId = Long.parseLong(req.getParameter("job_id"));

        ReadingStatusBO.Result result = statusBO.checkStatus(jobId, user.getId());
        if (result == null) {
            resp.sendError(404);
            return;
        }

        resp.setContentType("application/json; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");

        // Build JSON
        if ("SUCCEEDED".equals(result.status)) {
            resp.getWriter().write("""
                {"status":"SUCCEEDED","answer_letter":"%s","history_id":%d}
            """.formatted(result.answerLetter, result.historyId));

        } else if ("FAILED".equals(result.status)) {
            resp.getWriter().write("""
                {"status":"FAILED","error":%s}
            """.formatted(toJson(result.error)));

        } else {
            resp.getWriter().write("""
                {"status":"%s"}
            """.formatted(result.status));
        }
    }

    private static String toJson(String s) {
        return s == null ? "null" : "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
