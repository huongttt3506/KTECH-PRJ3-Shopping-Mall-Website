package com.example.ShoppingMall.jwt;

import com.example.ShoppingMall.user.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.ShoppingMall.user.entity.UserEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

//This class intercepts HTTP requests and checks for the presence of a valid JWT token
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        //1. Token Extraction
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        //If the header contains a Bearer token
        // Bearer eyJhbGciOiJIUzI1NiIsIn...
        //extracts the JWT token by authHeader.split(" ")[1]
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1];

            //2. Token Validation
            if (jwtTokenUtils.validate(token)) {
                //get username
                String username = jwtTokenUtils
                        .parseClaims(token)
                        .getSubject();
                //get role
                String role = jwtTokenUtils
                        .parseClaims(token)
                        .get("role", String.class);
                UserRole userRole = UserRole.valueOf(role);

                //create user base on username and role info
                UserEntity user = UserEntity.builder()
                        .username(username)
                        .role(userRole)
                        .build();

                //3. Set Authentication Context
                SecurityContext context
                        = SecurityContextHolder.createEmptyContext();
                // create authentication base on user and token
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        token,
                        Collections.singletonList(new SimpleGrantedAuthority(userRole.name())));

                // put authentication in context
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } else {
                //Token invalid
                log.warn("jwt validation failed");
            }
        }
        //4. Pass to Next Filter:
        filterChain.doFilter(request, response);
    }
}
