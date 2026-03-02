package com.example.eventmanagement_backend.security;

import jakarta.servlet.ServletException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    // Use a dedicated Logger in a production application
    private static final String TAG = "Unauthorized Error: ";

    @Override
    public void commence(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.err.println(TAG + "Responding with unauthorized error. Message - " + authException.getMessage());

        // Respond with HTTP 401 Unauthorized status
        response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
