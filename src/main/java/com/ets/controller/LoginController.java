package com.ets.controller;

import com.ets.model.User;
import com.ets.services.AuthenticationService;

import java.io.IOException;

/**
 * Acts as the bridge between the login UI ({@code LoginFrame}) and the
 * {@link AuthenticationService}.
 *
 * <p>Keeps the view free of business logic: the frame only collects input
 * and displays results, while all authentication decisions are delegated
 * to the service layer.</p>
 */
public class LoginController {

    private final AuthenticationService authService;

    public LoginController(AuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * Looks up a user by username.
     *
     * @param username the username entered by the user
     * @return the authenticated {@link User} on success, or {@code null} if not found
     */
    public User login(String username) {
        return authService.authenticate(username);
    }

    /**
     * Registers a new student account.
     *
     * @param username the desired username
     * @return the new {@link User}, or {@code null} if the username is already taken
     * @throws IOException if the user data cannot be persisted
     */
    public User register(String username) throws IOException {
        return authService.registerUser(username);
    }
}