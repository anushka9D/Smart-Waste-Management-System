package com.swms.security;

import com.swms.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT processing for public endpoints
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/api/auth/") ||
                requestPath.startsWith("/api/citizens/") ||
                requestPath.startsWith("/api/city-authorities/") ||

                (requestPath.startsWith("/api/drivers/") && !requestPath.startsWith("/api/routes/")) ||
                (requestPath.startsWith("/api/waste-collection-staff/") && !requestPath.startsWith("/api/routes/")) ||
                requestPath.startsWith("/api/sensor-managers/")) {

                requestPath.startsWith("/api/drivers/") ||
                requestPath.startsWith("/api/v1/smartbin/") ||
                requestPath.startsWith("/api/v1/bin-sensors/") ||
                requestPath.startsWith("/api/v1/alerts/") ||
                requestPath.startsWith("/api/waste-collection-staff/")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Log the request for debugging
        logger.info("Processing JWT authentication for: " + request.getMethod() + " " + requestPath);

        String token = null;
        String email = null;

        // Try to get token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.info("Token found in Authorization header");
        }

        // If not in header, try to get from cookie
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        logger.info("Token found in cookie");
                        break;
                    }
                }
            }
        }

        // Extract email from token
        if (token != null) {
            try {
                email = jwtUtil.getEmailFromToken(token);
                logger.info("Email extracted from token: " + email);
            } catch (Exception e) {
                logger.error("JWT Token validation error: " + e.getMessage());
            }
        }

        // Validate token and set authentication
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                logger.info("User details loaded for: " + email + " with authorities: " + userDetails.getAuthorities());

                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Authentication set for user: " + email);
                } else {
                    logger.warn("Token validation failed for user: " + email);
                }
            } catch (Exception e) {
                logger.error("Error loading user details for: " + email, e);
            }
        } else {
            if (email == null) {
                logger.info("No email found in token");
            } else {
                logger.info("Authentication already exists in SecurityContext");
            }
        }

        filterChain.doFilter(request, response);
    }
}