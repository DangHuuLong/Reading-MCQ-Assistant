<%@ page contentType="text/html; charset=UTF-8" %>
<%
    Object jobAttr = request.getAttribute("jobId");
    String jobId = jobAttr == null ? "" : String.valueOf(jobAttr);
    String ctx   = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Processing…</title>
<style>
  body {
    font-family: "Segoe UI", sans-serif;
    background-color: #f4f6f9;
    margin: 0;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .card {
    background: white;
    padding: 28px 32px;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    text-align: center;
    width: 480px;
  }
  h2 {
    margin-top: 0;
    color: #333;
  }
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 16px 0;
    padding: 10px 16px;
    border: 1px solid #ddd;
    border-radius: 6px;
    background: #fafafa;
  }
  .info-label {
    font-weight: 600;
    color: #555;
  }
  .info-value {
    font-weight: 700;
    font-size: 18px;
  }
  .status.pending { color: #f1c40f; }
  .status.running { color: #3498db; }
  .status.succeeded { color: #2ecc71; }
  .status.failed { color: #e74c3c; }
  .footer {
    margin-top: 20px;
  }
  a {
    display: inline-block;
    margin: 0 6px;
    padding: 8px 16px;
    border-radius: 6px;
    text-decoration: none;
    color: white;
    background-color: #4285f4;
  }
  a.secondary { background-color: #6c757d; }
  a:hover { opacity: 0.9; }
  .spinner {
    margin-top: 16px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #3498db;
    border-radius: 50%;
    width: 26px;
    height: 26px;
    animation: spin 1s linear infinite;
    display: inline-block;
  }
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
</style>
</head>
<body>
<div class="card">
  <h2>🧠 Reading MCQ - Processing</h2>
  <p>Job ID: <b><%= jobId %></b></p>

  <div class="info-row">
    <div class="info-label">Status</div>
    <div class="info-value status pending" id="statusText">Pending...</div>
  </div>

  <div class="info-row">
    <div class="info-label">Answer</div>
    <div class="info-value" id="answerText">-</div>
  </div>

  <div id="spinner" class="spinner"></div>

  <div class="footer">
    <a href="<%= ctx %>/reading.jsp">← New Question</a>
    <a href="<%= ctx %>/history" class="secondary">View History →</a>
  </div>
</div>

<script>
  const jobId = "<%= jobId %>";
  const base  = "<%= ctx %>";
  const statusEl = document.getElementById("statusText");
  const answerEl = document.getElementById("answerText");
  const spinner  = document.getElementById("spinner");

  async function poll() {
    try {
      const res = await fetch(base + "/reading/status?job_id=" + encodeURIComponent(jobId), { cache: "no-store" });
      if (!res.ok) throw new Error("HTTP " + res.status);
      const j = await res.json();

      if (j.status === "SUCCEEDED") {
        statusEl.textContent = "Succeeded";
        statusEl.className = "info-value status succeeded";
        answerEl.textContent = j.answer_letter || "?";
        spinner.style.display = "none";
        clearInterval(poller);
        return;
      }

      if (j.status === "FAILED") {
        statusEl.textContent = "Failed";
        statusEl.className = "info-value status failed";
        answerEl.textContent = "-";
        spinner.style.display = "none";
        clearInterval(poller);
        return;
      }

      // Update trạng thái đang xử lý
      const normalized = j.status.toLowerCase();
      statusEl.textContent = normalized.charAt(0).toUpperCase() + normalized.slice(1);
      statusEl.className = "info-value status " + normalized;
    } catch (e) {
      console.error(e);
      statusEl.textContent = "Error";
      statusEl.className = "info-value status failed";
      spinner.style.display = "none";
      clearInterval(poller);
    }
  }

  const poller = setInterval(poll, 2000);
  poll();
</script>
</body>
</html>
