package com.readingmcqassistant.controller;

import com.readingmcqassistant.dao.HistoryDAO;
import com.readingmcqassistant.model.History;
import com.readingmcqassistant.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reading")
public class ReadingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        String passage = request.getParameter("passage");
        String question = request.getParameter("question");
        String a = request.getParameter("optionA");
        String b = request.getParameter("optionB");
        String c = request.getParameter("optionC");
        String d = request.getParameter("optionD");

        // -------------------------------
        // 🔸 CHỖ GẮN LOGIC AI VÀO ĐÂY
        // Gọi hàm xử lý AI (Python, API, Model) để xác định đáp án đúng
        // Ví dụ tạm thời: chọn random hoặc fix cứng
        String correctAnswer = "A"; // TODO: replace with AI result
        // -------------------------------

        // Lưu lịch sử
        History history = new History(user.getId(), passage, question, a, b, c, d, correctAnswer);
        new HistoryDAO().addHistory(history);

        // Gửi kết quả ra lại trang đọc
        request.setAttribute("correctAnswer", correctAnswer);
        request.getRequestDispatcher("reading_result.jsp").forward(request, response);
    }
}
