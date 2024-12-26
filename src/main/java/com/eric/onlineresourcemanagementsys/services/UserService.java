package com.eric.onlineresourcemanagementsys.services;

import com.eric.onlineresourcemanagementsys.Entity.User;
import com.eric.onlineresourcemanagementsys.repos.UserRepository;
import com.eric.onlineresourcemanagementsys.utils.EncryptionUtil;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final SecretKey secretKey;

    public UserService(UserRepository userRepository, SecretKey secretKey) {
        this.userRepository = userRepository;
        this.secretKey = secretKey;
    }

    // Registering  a new user
    public ServiceResponse<User> registerUser(String username, String password) {
        List<String> errorMessages = new ArrayList<>();

        if (userRepository.findByUsername(username) != null) {
            errorMessages.add("Username already exists. Please log in instead.");
            return new ServiceResponse<>(null, false, "Registration failed", errorMessages);
        }

        try {
            byte[] encryptedPassword = EncryptionUtil.encryptPassword(password, secretKey);
            User user = new User(username, encryptedPassword);
            User savedUser = userRepository.save(user);
            return new ServiceResponse<>(savedUser, true, "Registration successful", null);
        } catch (Exception e) {
            errorMessages.add("Error during registration: " + e.getMessage());
            return new ServiceResponse<>(null, false, "Registration failed", errorMessages);
        }
    }

    // login a user
    public ServiceResponse<User> loginUser(String username, String password) {
        List<String> errorMessages = new ArrayList<>();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            errorMessages.add("Username not found. Please try again.");
            return new ServiceResponse<>(null, false, "Login failed", errorMessages);
        }

        try {
            if (user.verifyPassword(password, secretKey)) {
                return new ServiceResponse<>(user, true, "Login successful", null);
            } else {
                errorMessages.add("Invalid username or password.");
                return new ServiceResponse<>(null, false, "Login failed", errorMessages);
            }
        } catch (Exception e) {
            errorMessages.add("Error during password verification: " + e.getMessage());
            return new ServiceResponse<>(null, false, "Login failed", errorMessages);
        }
    }

    // Deleting a user
    public ServiceResponse<Void> deleteUser(int userId) {
        List<String> errorMessages = new ArrayList<>();

        try {
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return new ServiceResponse<>(null, true, "User deleted successfully", null);
            } else {
                errorMessages.add("User with ID " + userId + " does not exist.");
                return new ServiceResponse<>(null, false, "Deletion failed", errorMessages);
            }
        } catch (Exception e) {
            errorMessages.add("Error during user deletion: " + e.getMessage());
            return new ServiceResponse<>(null, false, "Deletion failed", errorMessages);
        }
    }


    public ServiceResponse<User> findUserById(int userId) {
        List<String> errorMessages = new ArrayList<>();
        try {
            return userRepository.findById(userId)
                    .map(user -> new ServiceResponse<>(user, true, "User found", null))
                    .orElseGet(() -> new ServiceResponse<>(null, false, "User not found", List.of("No user found with ID: " + userId)));
        } catch (Exception e) {
            errorMessages.add("Error retrieving user: " + e.getMessage());
            return new ServiceResponse<>(null, false, "Failed to retrieve user", errorMessages);
        }
    }

}
