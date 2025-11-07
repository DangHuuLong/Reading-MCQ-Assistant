<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.readingmcqassistant.model.History"%>
<%
    List<History> histories = (List<History>) request.getAttribute("histories");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reading History</title>
</head>
<body>
    <h2>Your Reading History</h2>
    <form method="post" action="history">
        <table border="1" cellpadding="8" cellspacing="0">
            <tr>
                <th>Select</th>
                <th>Question</th>
                <th>Correct Answer</th>
                <th>Created At</th>
            </tr>
            <%
                if (histories != null) {
                    for (History h : histories) {
            %>
            <tr>
                <td><input type="checkbox" name="selected" value="<%=h.getId()%>"></td>
                <td><%=h.getQuestion()%></td>
                <td><%=h.getCorrectAnswer()%></td>
                <td><%=h.getCreatedAt()%></td>
            </tr>
            <% } } %>
        </table>
        <button type="submit">Delete Selected</button>
    </form>
    <a href="reading.jsp">Back to Reading</a>
</body>
</html>
