package com.api.apicheck_incheck_out.securitytest;

import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.exceptionhandling.EncryptionException;
import com.api.apicheck_incheck_out.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class JwtServiceTest {

    private String base64Key;

    private String makeBase64SecretKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @Test
    void generateToken_and_extractUserName_shouldReturnEmail() {
        base64Key = makeBase64SecretKey();
        JwtService jwtService = new JwtService();

        User user = new User();
        user.setId(1L);
        user.setEmail("alami@gmail.com");
        user.setNom("Nom");
        user.setPrenom("Prenom");
        user.setRole(Role.CLIENT);

        String token = jwtService.generateToken(user);
        assertNotNull(token);
        String extracted = jwtService.extractUserName(token);
        assertEquals("alami@gmail.com", extracted);
    }

    @Test
    void validateToken_withMatchingUser_shouldReturnTrue() {
        base64Key = makeBase64SecretKey();
        JwtService jwtService = new JwtService();

        User user = new User();
        user.setEmail("alami@gmail.com");
        user.setRole(Role.CLIENT);
        String token = jwtService.generateToken(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("alami@gmail.com");

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void isTokenExpired_withExpiredToken_shouldReturnTrue() {
        base64Key = makeBase64SecretKey();
        JwtService jwtService = new JwtService();
        jwtService.secretKey=base64Key;

        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
        String expiredToken = Jwts.builder()
                .setSubject("alami@gmail.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 2))
                .setExpiration(new Date(System.currentTimeMillis() - 1000L))
                .signWith(key)
                .compact();

        assertTrue(jwtService.isTokenExpired(expiredToken));
    }
    @Test
    void isTokenExpired_withExpiredToken_shouldReturnFalse() {
        base64Key = makeBase64SecretKey();
        JwtService jwtService = new JwtService();
        jwtService.secretKey=base64Key;

        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
        String expiredToken = Jwts.builder()
                .setSubject("alami@gmail.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 10))
                .signWith(key)
                .compact();

        assertFalse(jwtService.isTokenExpired(expiredToken));
    }

    @Test
    void validateToken_withWrongUsername_shouldReturnFalse() {
        base64Key = makeBase64SecretKey();
        JwtService jwtService = new JwtService();
        jwtService.secretKey=base64Key;

        User user = new User();
        user.setEmail("alami@gmail.com");
        user.setRole(Role.CLIENT);
        String token = jwtService.generateToken(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("ali@gmail.com");

        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void invalidToken_shouldThrow_whenExtracting() {
        base64Key = makeBase64SecretKey();
        JwtService jwtService = new JwtService();
        jwtService.secretKey=base64Key;

        String invalidToken = "invalid.token";

        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractUserName(invalidToken));
    }

    @Test
    void constructor_shouldThrowEncryptionException_whenNoSuchAlgorithm() {
        try (var mocked = mockStatic(KeyGenerator.class)) {
            mocked.when(() -> KeyGenerator.getInstance("HmacSHA256"))
                    .thenThrow(new NoSuchAlgorithmException("Algorithm not found"));

            assertThrows(EncryptionException.class, JwtService::new);

        }
    }
}
