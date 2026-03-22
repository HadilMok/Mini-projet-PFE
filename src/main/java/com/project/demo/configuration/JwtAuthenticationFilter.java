package com.project.demo.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private  UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {

            filterChain.doFilter(request, response);


//            // Skip filter for user authentication endpoints
//            if (request.getServletPath().contains("/api/v1/public")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            if (request.getServletPath().contains("/api/v1/test")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//            if (request.getServletPath().contains("/swagger-ui/**")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            if (request.getServletPath().contains("/v3/api-docs*/**")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            final String authHeader = request.getHeader("Authorization");
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                response.setCharacterEncoding("UTF-8");
//                response.getWriter().write("{\"message\":\"Missing or invalid Authorization header\"}");
//                return;
//            }
//
//            final String jwt = authHeader.substring(7);
//            final String userEmail = jwtService.extractUserEmail(jwt);
//
//            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                try {
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//
//                    if (jwtService.isTokenValid(jwt, userDetails)) {
//                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//                        authToken.setDetails(
//                                new WebAuthenticationDetailsSource().buildDetails(request)
//                        );
//                        SecurityContextHolder.getContext().setAuthentication(authToken);
//                    } else {
//                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                        response.setContentType("application/json");
//                        response.setCharacterEncoding("UTF-8");
//                        response.getWriter().write("{\"message\":\"Invalid token\"}");
//                        return;
//                    }
//                } catch (Exception e) {
//                    // Handle any exceptions during user details loading or token validation
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    response.setContentType("application/json");
//                    response.setCharacterEncoding("UTF-8");
//                    response.getWriter().write("{\"message\":\"Authentication error\"}");
//                    return;
//                }
//            }
//            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Handle unexpected exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Internal server error +"+e.getMessage()+e.getCause()+"\"}");
        }

    }
}
