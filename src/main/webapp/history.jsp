<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*,com.readingmcqassistant.model.bean.History"%>
<%
    List<History> histories = (List<History>) request.getAttribute("histories");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reading History</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f9;
            margin: 0;
            padding: 40px 0;
            display: flex;
            justify-content: center;
        }
        .container {
            width: 1000px;
            background-color: #fff;
            padding: 24px 28px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        h2 { margin-top: 0; color: #333; margin-bottom: 18px; }
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th, td { padding: 10px 8px; border-bottom: 1px solid #eee; vertical-align: top; }
        th { text-align: left; background-color: #f1f3f5; font-weight: bold; }
        tr:hover { background-color: #fafbff; }
        .question { font-weight: 500; color: #333; }
        .meta { font-size: 11px; color: #777; }
        .actions { margin-top: 14px; display: flex; justify-content: space-between; align-items: center; gap: 8px; }
        button {
            padding: 8px 16px; background-color: #d9534f; color: #fff;
            border: none; border-radius: 4px; font-size: 13px; cursor: pointer;
        }
        button:hover { background-color: #c9302c; }
        .links a { font-size: 13px; text-decoration: none; color: #4285f4; margin-right: 10px; }
        .links a:hover { text-decoration: underline; }
        .empty { padding: 16px 0; color: #777; font-size: 13px; }

        details summary {
            cursor: pointer;
            color: #007bff;
            font-size: 12px;
            margin-top: 4px;
        }
        details div {
            margin-top: 6px;
            background: #fafafa;
            padding: 8px;
            border-radius: 4px;
            white-space: pre-line;
        }
    </style>
</head>
<body>
<div class="container">
  <h2>Your Reading History</h2>

  <form method="post" action="<%= request.getContextPath() %>/history">
    <table>
      <tr>
        <th style="width:40px;">
          <input type="checkbox" onclick="
            document.querySelectorAll('.row-check').forEach(cb => cb.checked = this.checked);
          " />
        </th>
        <th>Question</th>
        <th>Passage</th>
        <th>Correct</th>
        <th>Created At</th>
      </tr>

      <%
        if (histories != null && !histories.isEmpty()) {
            for (History h : histories) {
      %>
          <tr>
            <td>
              <input type="checkbox" class="row-check" name="selected" value="<%= h.getId() %>">
            </td>

            <td style="width:35%;">
              <div class="question"><%= h.getQuestion() %></div>
              <div class="meta">
                A: <%= h.getOptionA() %> |
                B: <%= h.getOptionB() %> |
                C: <%= h.getOptionC() %> |
                D: <%= h.getOptionD() %>
              </div>
            </td>

            <td style="width:30%;">
              <details>
                <summary>Show passage</summary>
                <div><%= h.getPassage() %></div>
              </details>
            </td>

            <td><%= h.getCorrectAnswer() %></td>
            <td><%= h.getCreatedAt() %></td>
          </tr>
      <%
            } // end for
        } else {
      %>

      <tr>
        <td colspan="5" class="empty">
          No history yet. Try creating a question in Reading MCQ.
        </td>
      </tr>

      <% } %>

    </table>

    <div class="actions">
      <button type="submit">Delete selected</button>
      <div class="links">
        <a href="<%= request.getContextPath() %>/reading.jsp">← Go to Reading</a>
        <a href="<%= request.getContextPath() %>/home.jsp">Back to Home</a>
      </div>
    </div>
  </form>
</div>
</body>
</html>
