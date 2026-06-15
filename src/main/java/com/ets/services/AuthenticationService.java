package com.ets.services;

import com.ets.model.User;
import com.ets.repo.UserRepository;

/**
 * Handles user authentication for the Educational Testing System.
 *
 * <p>Validates username and password against the {@link UserRepository}
 * and returns the authenticated {@link User} on success.</p>
 */
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Attempts to authenticate a user with the supplied credentials.
     *
     * @param username the plain-text username
     * @param password the plain-text password
     * @return the authenticated {@link User} if credentials match, or {@code null} otherwise
     */
    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        User user = userRepository.findByUsername(username.trim());
        if (user == null) {
            return null; // Username not found
        }

        if (user.getPassword().equals(password)) {
            return user; // Credentials match
        }

        return null; // Wrong password
    }
}
