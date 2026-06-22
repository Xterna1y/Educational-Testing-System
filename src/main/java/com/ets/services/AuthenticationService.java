package com.ets.services;

import com.ets.model.User;
import com.ets.repo.UserRepository;

import java.io.IOException;

/**
 * Handles user authentication and registration for the Educational Testing System.
 *
 * <p>Looks up users in {@link UserRepository}. Authentication requires only a
 * valid username (no password). Both {@code authenticate} and {@code registerUser}
 * accept an optional role so the Student and Admin login screens stay independent.</p>
 */
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Attempts to authenticate a user by username only (any role).
     *
     * @param username the username entered by the user
     * @return the authenticated {@link User} if found, or {@code null} otherwise
     */
    public User authenticate(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username.trim());
    }

    /**
     * Attempts to authenticate a user by username, restricted to the given role.
     *
     * <p>Returns {@code null} if the user exists but has a different role,
     * keeping Student and Admin login screens fully isolated.</p>
     *
     * @param username the username entered by the user
     * @param role     the required role — {@code "STUDENT"} or {@code "ADMIN"}
     * @return the authenticated {@link User} if found with matching role, or {@code null}
     */
    public User authenticate(String username, String role) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        User user = userRepository.findByUsername(username.trim());
        if (user == null) {
            return null; // username not found
        }
        if (!user.getRole().equalsIgnoreCase(role)) {
            return null; // wrong portal — correct role required
        }
        return user;
    }

    /**
     * Registers a new STUDENT account (convenience overload).
     *
     * @param username the desired username
     * @return the newly created {@link User}, or {@code null} if username is already taken
     * @throws IOException if the user data cannot be saved
     */
    public User registerUser(String username) throws IOException {
        return registerUser(username, "STUDENT");
    }

    /**
     * Registers a new account with the specified role.
     *
     * @param username the desired username
     * @param role     the role to assign — {@code "STUDENT"} or {@code "ADMIN"}
     * @return the newly created {@link User}, or {@code null} if username is already taken
     * @throws IOException if the user data cannot be saved
     */
    public User registerUser(String username, String role) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.registerUser(username.trim(), role);
    }
}
