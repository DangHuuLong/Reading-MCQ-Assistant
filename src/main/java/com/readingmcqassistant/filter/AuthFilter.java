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

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());

        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        boolean isPublicPath =
                path.equals("/") ||
                path.equals("/login.jsp") ||
                path.equals("/register.jsp") ||
                path.equals("/login") ||
                path.equals("/register") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/");

        if (loggedIn) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            chain.doFilter(req, res);
        } else if (isPublicPath) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(contextPath + "/login.jsp");
        }
    }
}
