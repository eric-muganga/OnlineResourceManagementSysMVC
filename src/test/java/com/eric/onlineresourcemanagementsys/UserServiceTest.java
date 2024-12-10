package com.eric.onlineresourcemanagementsys;

import com.eric.onlineresourcemanagementsys.Entity.User;
import com.eric.onlineresourcemanagementsys.repos.UserRepository;
import com.eric.onlineresourcemanagementsys.services.UserService;
import com.eric.onlineresourcemanagementsys.utils.KeyHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        SecretKey secretKey = KeyHandler.generateSecretKey();
        userService = new UserService(userRepository, secretKey);
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully_WhenUsernameIsAvailable() {
        // Arrange
        String username = "ericTest";
        String password = "password123";
        when(userRepository.findByUsername(username)).thenReturn(null); // Simulate no existing user
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Simulate user save

        // Act
        User registeredUser = userService.registerUser(username, password);

        // Assert
        assertNotNull(registeredUser, "The registered user should not be null");
        assertEquals(username, registeredUser.getUsername(), "The username should match the input");
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        String existingUsername = "ericTest";
        when(userRepository.findByUsername(existingUsername)).thenReturn(new User()); // Simulate existing user

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(existingUsername, "password123"),
                "Expected exception for duplicate username"
        );

        assertEquals("Username already exists. Please log in instead.", exception.getMessage(),
                "Exception message should indicate duplicate username");
    }
}
