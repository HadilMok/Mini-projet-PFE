package com.project.demo.configuration;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final JwtService jwtService;

    private static final Logger logger = Logger.getLogger(CustomAccessDeniedHandler.class.getName());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        String requiredRole = getRequiredRoleFromRequest(request);
        String message = "You need to be " + requiredRole.toLowerCase() + " to access this endpoint";


        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Missing or invalid Authorization header\"}");
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUserEmail(jwt);
        final String x=jwtService.getClaim(jwt, Claims::getSubject);


        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().write("{\"error\": \"" + message + "\"}");
        } catch (IOException e) {
            // Log the error in case of an issue writing the response
            logger.log(Level.SEVERE, "Error writing the access denied response", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private String getRequiredRoleFromRequest(HttpServletRequest request) {
        // Here, you can add logic to determine the required role based on the request path
        String servletPath = request.getServletPath();
        if (servletPath.contains("/protected/")) {
            return "Admin";
        } else if (servletPath.contains("/gym/")) {
            return "Gym";
        } else if (servletPath.contains("/user/")) {
            return "User";
        }
        // Default role if not matched
        return "User";
    }
}
