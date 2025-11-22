package com.readingmcqassistant.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request  = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    final String ctx  = request.getContextPath();
    String path = request.getRequestURI().substring(ctx.length());
    if (path.isEmpty()) path = "/";

    HttpSession session = request.getSession(false);
    boolean loggedIn = (session != null && session.getAttribute("user") != null);

    // Xác định request AJAX/JSON để trả 401 đúng kiểu
    String accept = request.getHeader("Accept");
    boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                     || (accept != null && accept.contains("application/json"));

    // Cho phép public paths (login/register + static)
    boolean isPublicPath =
        path.equals("/") ||
        path.equals("/login.jsp") ||
        path.equals("/register.jsp") ||
        path.equals("/login") ||
        path.equals("/register") ||
        path.startsWith("/css/") ||
        path.startsWith("/js/") ||
        path.startsWith("/images/") ||
        path.equals("/favicon.ico") ||
        path.startsWith("/webjars/");

    if (loggedIn || isPublicPath) {
      // no-store cho trang sau đăng nhập (tránh back history)
      if (loggedIn) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
      }
      chain.doFilter(req, res);
      return;
    }

    // Chưa đăng nhập
    if (isAjax) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json; charset=UTF-8");
      response.getWriter().write("{\"error\":\"UNAUTHORIZED\"}");
    } else {
      response.sendRedirect(ctx + "/login.jsp");
    }
  }
}

