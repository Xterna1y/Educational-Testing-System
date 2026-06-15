package com.ets.controller;

import com.ets.model.User;
import com.ets.services.AuthenticationService;

/**
 * Acts as the bridge between the Login UI ({@code LoginFrame})
 * and the {@link AuthenticationService}.
 *
 * <p>The controller keeps the view and service layers decoupled:
 * the view calls {@link #login(String, String)} and reacts to
 * the returned result without knowing how authentication works.</p>
 */
public class LoginController {

    private final AuthenticationService authService;

    public LoginController(AuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * Validates the supplied credentials.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return the authenticated {@link User} on success, or {@code null} on failure
     */
    public User login(String username, String password) {
        return authService.authenticate(username, password);
    }
}
