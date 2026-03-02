package com.example.eventmanagement_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getEmailFromJwtToken(jwt);

                // Load user from DB / UserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create Authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set to SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Don't crash the app; just continue without authentication
            System.err.println("JWT authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Skip JWT filter for public endpoints.
     * This avoids AntPathRequestMatcher completely.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getServletPath(); // better than getRequestURI() when context-path exists

        // Public auth endpoints
        if ("POST".equals(method) && ("/api/auth/signin".equals(path) || "/api/auth/signup".equals(path))) {
            return true;
        }

        // Public GET endpoints
        if ("GET".equals(method)) {
            if (path.startsWith("/api/events")) return true;
            if (path.startsWith("/api/speakers")) return true;
            if (path.startsWith("/api/registrations/event")) return true;
        }

        return false;
    }

    /**
     * Extract JWT token from Authorization header: "Bearer <token>"
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}