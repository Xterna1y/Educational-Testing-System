package com.ets.services;

import com.ets.model.User;
import com.ets.repo.UserRepository;

import java.io.IOException;

/**
 * Handles user authentication and registration for the Educational Testing System.
 *
 * <p>Looks up users in {@link UserRepository}. Authentication requires only a
 * valid username (no password). Registration checks for duplicates and persists
 * the new user to disk.</p>
 */
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Attempts to authenticate a user by username only.
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
     * Registers a new student account.
     *
     * @param username the desired username
     * @return the newly created {@link User}, or {@code null} if username is already taken
     * @throws IOException if the user data cannot be saved
     */
    public User registerUser(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.registerUser(username.trim());
    }
}
