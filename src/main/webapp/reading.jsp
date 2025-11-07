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
        a.back {
            display: inline-block;
            margin-top: 14px;
            text-decoration: none;
            color: #666;
            font-size: 13px;
        }
        a.back:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Reading MCQ</h2>

    <form action="reading" method="post">
        <label>Passage</label>
        <textarea name="passage" required></textarea>

        <label>Question</label>
        <input type="text" name="question" required>

        <div class="options">
            <div>
                <label>Option A</label>
                <input type="text" name="optionA" required>
            </div>
            <div>
                <label>Option B</label>
                <input type="text" name="optionB" required>
            </div>
            <div>
                <label>Option C</label>
                <input type="text" name="optionC" required>
            </div>
            <div>
                <label>Option D</label>
                <input type="text" name="optionD" required>
            </div>
        </div>

        <button type="submit">Analyze Answer</button>
    </form>

    <a href="home.jsp" class="back">Back to Home</a>
</div>
</body>
</html>
