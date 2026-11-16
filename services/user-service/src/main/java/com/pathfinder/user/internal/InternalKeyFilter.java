package com.pathfinder.user.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalKeyFilter extends OncePerRequestFilter {
    @Value("${app.internal-key}")
    private String key;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        return !req.getRequestURI().startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (!key.equals(req.getHeader("X-Internal-Key"))) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Invalid internal key.\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}