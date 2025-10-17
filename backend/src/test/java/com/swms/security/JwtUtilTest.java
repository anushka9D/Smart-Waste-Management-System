package com.swms.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String secret = "mySecretKeyForTestingPurposesOnly123456789012345"; // Longer secret for HS512
    private Long expiration = 3600000L; // 1 hour in milliseconds

    private String userId = "user123";
    private String name = "John Doe";
    private String email = "john.doe@example.com";
    private String userType = "CITIZEN";
    private String phone = "1234567890";

    @BeforeEach
    void setUp() {
        // Set the secret and expiration values using reflection
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Test generateToken method - Positive Cases
    @Test
    void testGenerateToken_Positive() {
        // Act
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // Test getPhoneFromToken method - Positive Cases
    @Test
    void testGetPhoneFromToken_Positive() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        String extractedPhone = jwtUtil.getPhoneFromToken(token);

        // Assert
        assertEquals(phone, extractedPhone);
    }

    // Test getEmailFromToken method - Positive Cases
    @Test
    void testGetEmailFromToken_Positive() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    // Test getUserIdFromToken method - Positive Cases
    @Test
    void testGetUserIdFromToken_Positive() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(token);

        // Assert
        assertEquals(userId, extractedUserId);
    }

    // Test getNameFromToken method - Positive Cases
    @Test
    void testGetNameFromToken_Positive() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        String extractedName = jwtUtil.getNameFromToken(token);

        // Assert
        assertEquals(name, extractedName);
    }

    // Test getUserTypeFromToken method - Positive Cases
    @Test
    void testGetUserTypeFromToken_Positive() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        String extractedUserType = jwtUtil.getUserTypeFromToken(token);

        // Assert
        assertEquals(userType, extractedUserType);
    }

    // Test getExpirationDateFromToken method - Positive Cases
    @Test
    void testGetExpirationDateFromToken_Positive() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    // Test isTokenExpired method - Positive Cases
    @Test
    void testIsTokenExpired_FreshToken() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        Boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    // Test isTokenExpired method - Edge Cases
    @Test
    void testIsTokenExpired_ExpiredToken() {
        // Arrange - Create an expired token manually using the same signing method
        String expiredToken = Jwts.builder()
                .subject(email)
                .expiration(new Date(System.currentTimeMillis() - 10000)) // 10 seconds in the past
                .signWith(getSigningKey())
                .compact();

        // Act
        Boolean isExpired = jwtUtil.isTokenExpired(expiredToken);

        // Assert
        assertTrue(isExpired);
    }

    // Test validateToken method - Positive Cases
    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        Boolean isValid = jwtUtil.validateToken(token, email);

        // Assert
        assertTrue(isValid);
    }

    // Test validateToken method - Negative Cases
    @Test
    void testValidateToken_InvalidEmail() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        Boolean isValid = jwtUtil.validateToken(token, "wrong@example.com");

        // Assert
        assertFalse(isValid);
    }

    // Test validateToken method - Edge Cases
    @Test
    void testValidateToken_ExpiredToken() {
        // Arrange - Create an expired token manually using the same signing method
        String expiredToken = Jwts.builder()
                .subject(email)
                .expiration(new Date(System.currentTimeMillis() - 10000)) // 10 seconds in the past
                .signWith(getSigningKey())
                .compact();

        // Act
        Boolean isValid = jwtUtil.validateToken(expiredToken, email);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken() {
        // Act
        Boolean isValid = jwtUtil.validateToken(null, email);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullEmail() {
        // Arrange
        String token = jwtUtil.generateToken(userId, name, email, userType, phone);

        // Act
        Boolean isValid = jwtUtil.validateToken(token, null);

        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testValidateToken_EmptyToken() {
        // Act
        Boolean isValid = jwtUtil.validateToken("", email);

        // Assert
        assertFalse(isValid);
    }

    // Test getUserIdFromToken method - Edge Cases
    @Test
    void testGetUserIdFromToken_InvalidToken() {
        // Act
        String userId = jwtUtil.getUserIdFromToken("invalid.token.string");

        // Assert
        assertNull(userId);
    }

    // Test getEmailFromToken method - Edge Cases
    @Test
    void testGetEmailFromToken_InvalidToken() {
        // Act
        String email = jwtUtil.getEmailFromToken("invalid.token.string");

        // Assert
        assertNull(email);
    }

    // Test getPhoneFromToken method - Edge Cases
    @Test
    void testGetPhoneFromToken_InvalidToken() {
        // Act
        String phone = jwtUtil.getPhoneFromToken("invalid.token.string");

        // Assert
        assertNull(phone);
    }

    // Error case tests
    @Test
    void testGenerateToken_NullValues() {
        // Act
        String token = jwtUtil.generateToken(null, null, email, null, null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}