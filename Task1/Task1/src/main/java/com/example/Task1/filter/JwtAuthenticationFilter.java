package com.example.Task1.filter;

import com.example.Task1.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Spring;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
                                    // THIS IS CALLED ON EVERY REQUEST to your application
// Runs BEFORE your controller methods
        final String authorizationHeader = request.getHeader("Authorization");
        
        String email = null;
        String jwt = null;
        String userType = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
                userType = jwtUtil.extractUserType(jwt);
            } catch (Exception e) {
                logger.error("invalid jwt token: {}", e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, email)) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userType);
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, null, Arrays.asList(authority));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
// Line 44: Validates the JWT token (checks signature, expiration, etc.)
// Line 45: Creates a Spring Security authority with "ROLE_" prefix
// If userType = "ADMIN", creates "ROLE_ADMIN"
// Line 46-47: Creates authentication token with user's email and role
// Line 48: SETS THE AUTHENTICATION in Spring Security context
        }
        
        filterChain.doFilter(request, response);
    }
    // CRITICAL: Passes the request to the next filter/controller
// At this point, Spring Security knows who the user is and what role they have
}
