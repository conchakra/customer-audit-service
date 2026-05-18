package com.project.findisc.audit_table.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component   // ✅ REQUIRED
@Profile("!test")
public class AuthFilter implements Filter {

    @Autowired
    private AuthServiceClient authService;

    @Override
public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    String path = req.getRequestURI();

    // ✅ Skip token check for customer APIs
    if (path.startsWith("/customer/api/v1/customers")
        || path.startsWith("/files/")) {

    chain.doFilter(request, response);
    return;
}

    String authHeader = req.getHeader("Authorization");

    if (authHeader == null) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("Missing Token");
        return;
    }

    String token = authHeader.replace("Bearer ", "");

    boolean valid = authService.verifyToken(token);

    if (!valid) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("Invalid Token");
        return;
    }

    chain.doFilter(request, response);
}
}