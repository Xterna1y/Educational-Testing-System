package com.ets.model;

/**
 * Abstract base class representing a user in the Educational Testing System.
 * <p>
 * Defines the common properties and behaviour shared by all user types
 * (currently {@link Student}). It stores the login identity and a role
 * identifier that determines which parts of the system a user may access.
 * </p>
 *
 * @author ETS Team
 * @version 1.0
 */
public abstract class User {
    protected String username;
    protected String role;

    /**
     * Constructs a new {@code User} with the specified identity and role.
     *
     * @param username the unique login name of the user
     * @param role     a string constant identifying the user's role (e.g. {@code "STUDENT"})
     */
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    /**
     * Returns the username of this user.
     *
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * Returns the role assigned to this user.
     *
     * @return the role string (e.g. {@code "STUDENT"})
     */
    public String getRole() { return role; }
}