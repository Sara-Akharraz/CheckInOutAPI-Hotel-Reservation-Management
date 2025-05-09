//package com.api.apicheck_incheck_out.Security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    @Override
//    protected void doFilterInternal(
//            @NotNull HttpServletRequest request,
//            @NotNull HttpServletResponse response,
//            @NotNull FilterChain filterChain)
//            throws ServletException, IOException {
//                final String authHeader = request.getHeader("Authorization");
//                final String jwt;
//                final String userEmail;
//                if(authHeader == null || authHeader.startsWith("Bearer ")) {
//                    return;
//                }
//                jwt = authHeader.substring(7);
//                userEmail = jwtService.extractUsername();
//    }
//}
