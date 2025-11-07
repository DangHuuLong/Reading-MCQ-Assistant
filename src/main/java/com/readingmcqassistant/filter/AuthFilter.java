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

        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        boolean isAuthRequest =
                uri.endsWith("login.jsp") ||
                uri.endsWith("register.jsp") ||
                uri.endsWith("login") ||
                uri.endsWith("register") ||
                uri.contains("css") ||
                uri.contains("js") ||
                uri.contains("images");

        if (loggedIn) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            chain.doFilter(req, res);
        } else if (isAuthRequest) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect("login.jsp");
        }
    }
}
