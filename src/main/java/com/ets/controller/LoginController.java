package com.ets.controller;

import com.ets.model.User;
import com.ets.services.AuthenticationService;

import java.io.IOException;

/**
 * Acts as the bridge between the Login UI ({@code LoginFrame})
 * and the {@link AuthenticationService}.
 *
 * <p>The controller keeps the view and service layers decoupled:
 * the view calls {@link #login(String)} or {@link #register(String)}
 * and reacts to the returned result without knowing how auth works.</p>
 */
public class LoginController {

    private final AuthenticationService authService;

    public LoginController(AuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * Looks up a user by username (no password required).
     *
     * @param username the username entered by the user
     * @return the authenticated {@link User} on success, or {@code null} if not found
     */
    public User login(String username) {
        return authService.authenticate(username);
    }

    /**
     * Registers a new student account with the given username.
     *
     * @param username the desired username
     * @return the new {@link User}, or {@code null} if the username is already taken
     * @throws IOException if the user data cannot be persisted
     */
    public User register(String username) throws IOException {
        return authService.registerUser(username);
    }
}
