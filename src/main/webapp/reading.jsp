<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reading MCQ</title>
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
            width: 800px;
            background-color: #fff;
            padding: 24px 28px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        h2 {
            margin-top: 0;
            color: #333;
        }
        label {
            display: block;
            font-weight: bold;
            margin: 14px 0 6px;
        }
        textarea, input[type="text"] {
            width: 100%;
            padding: 10px;
            border-radius: 4px;
            border: 1px solid #ccc;
            font-size: 14px;
        }
        textarea {
            min-height: 120px;
            resize: vertical;
        }
        .options {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px 16px;
            margin-top: 10px;
        }
        .options div label {
            margin: 0 0 4px;
        }
        button {
            margin-top: 20px;
            padding: 10px 22px;
            background-color: #4285f4;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 15px;
            cursor: pointer;
        }
        button:hover {
            background-color: #3367d6;
        }
        .nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
        }
        a {
            text-decoration: none;
            color: #4285f4;
            font-size: 14px;
        }
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
  <h2>Reading MCQ</h2>

  <form action="${pageContext.request.contextPath}/reading" method="post" accept-charset="UTF-8">

    <!-- <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"> -->
    <label>Passage</label>
    <textarea name="passage" required maxlength="20000"></textarea>

    <label>Question</label>
    <input type="text" name="question" required maxlength="1000">

    <div class="options">
      <div><label>Option A</label><input type="text" name="optionA" required maxlength="500"></div>
      <div><label>Option B</label><input type="text" name="optionB" required maxlength="500"></div>
      <div><label>Option C</label><input type="text" name="optionC" required maxlength="500"></div>
      <div><label>Option D</label><input type="text" name="optionD" required maxlength="500"></div>
    </div>

    <button type="submit">Analyze Answer</button>
  </form>

  <div class="nav">
    <a  href="${pageContext.request.contextPath}/home.jsp">← Back to Home</a>
    <a  href="${pageContext.request.contextPath}/history">View History →</a>
  </div>
</div>
</body>
</html>