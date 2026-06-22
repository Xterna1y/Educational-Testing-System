package com.ets.controller;

import com.ets.model.User;
import com.ets.services.AuthenticationService;

import java.io.IOException;

/**
 * Acts as the bridge between the login UI frames ({@code LoginFrame},
 * {@code AdminLoginFrame}) and the {@link AuthenticationService}.
 *
 * <p>Both the Student and Admin screens share this single controller.
 * Each call passes its own role string so the service can keep the
 * two portals completely isolated.</p>
 */
public class LoginController {

    private final AuthenticationService authService;

    public LoginController(AuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * Looks up a user by username, restricted to the given role.
     *
     * @param username the username entered by the user
     * @param role     required role — {@code "STUDENT"} or {@code "ADMIN"}
     * @return the authenticated {@link User} on success, or {@code null} if not found
     *         or the role does not match
     */
    public User login(String username, String role) {
        return authService.authenticate(username, role);
    }

    /**
     * Registers a new account with the specified role.
     *
     * @param username the desired username
     * @param role     the role to assign — {@code "STUDENT"} or {@code "ADMIN"}
     * @return the new {@link User}, or {@code null} if the username is already taken
     * @throws IOException if the user data cannot be persisted
     */
    public User register(String username, String role) throws IOException {
        return authService.registerUser(username, role);
    }
}
