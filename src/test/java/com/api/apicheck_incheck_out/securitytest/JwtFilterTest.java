package com.api.apicheck_incheck_out.securitytest;

import com.api.apicheck_incheck_out.security.CustomUserDetailsService;
import com.api.apicheck_incheck_out.security.JWTFilter;
import com.api.apicheck_incheck_out.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JWTFilterTest {

    private JWTFilter jwtFilter;
    private JwtService jwtService;
    private ApplicationContext context;
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        context = mock(ApplicationContext.class);
        userDetailsService = mock(UserDetailsService.class);

        jwtFilter = new JWTFilter(jwtService, context);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_excludedPaths_shouldReturnTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getServletPath()).thenReturn("/api/user/login");

        boolean result = jwtFilter.shouldNotFilter(request);

        assertTrue(result);
    }

    @Test
    void doFilterInternal_validToken_shouldAuthenticateUser() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String token = "validToken";
        String email = "alami@gmail.com";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUserName(token)).thenReturn(email);
        CustomUserDetailsService customUserDetailsService = mock(CustomUserDetailsService.class);
        when(context.getBean(CustomUserDetailsService.class)).thenReturn(customUserDetailsService);
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_expiredToken_shouldNotAuthenticateUser() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        UserDetails userDetails = mock(UserDetails.class);
        String token = "expiredToken";
        String email = "alami@gmail.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUserName(token)).thenReturn(email);
        CustomUserDetailsService customUserDetailsService = mock(CustomUserDetailsService.class);
        when(context.getBean(CustomUserDetailsService.class)).thenReturn(customUserDetailsService);
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(mock(UserDetails.class));
        when(jwtService.validateToken(eq(token), any(UserDetails.class))).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

}
