<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reading Result</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f9;
            margin: 0;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background-color: #fff;
            padding: 28px 32px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            width: 420px;
            text-align: center;
        }
        h2 {
            margin-top: 0;
            color: #333;
            margin-bottom: 16px;
        }
        .answer {
            font-size: 18px;
            margin: 10px 0 20px;
        }
        .answer span {
            font-weight: bold;
            color: #4285f4;
        }
        a {
            display: inline-block;
            margin: 6px;
            padding: 8px 16px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 14px;
            background-color: #4285f4;
            color: #fff;
        }
        a.secondary {
            background-color: #6c757d;
        }
        a:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body>
<div class="container">
  <h2>Result</h2>
  <div class="answer">
    Correct Answer:
    <span>${param.answer}</span>
  </div>
  <a href="${pageContext.request.contextPath}/reading.jsp">New Question</a>
  <a href="${pageContext.request.contextPath}/history" class="secondary">View History</a>
</div>
</body>
</html>
