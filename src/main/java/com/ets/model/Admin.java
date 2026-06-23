package com.ets.model;

/**
 * Represents an administrator user in the Educational Testing System.
 * <p>
 * An {@code Admin} is a specialised {@link User} with the {@code "ADMIN"} role,
 * granting access to privileged operations such as managing questions, exams,
 * and student accounts.
 * </p>
 *
 * @author ETS Team
 * @version 1.0
 * @see User
 */
public class Admin extends User {

    /**
     * Constructs a new {@code Admin} with the given credentials.
     * <p>
     * The role is automatically set to {@code "ADMIN"} regardless of the value
     * passed for the {@code role} parameter.
     * </p>
     *
     * @param adminName the login name for this administrator
     * @param password  the password for this administrator
     * @param role      ignored; the role is always set to {@code "ADMIN"}
     */
    public Admin(String adminName, String password, String role) {
        super(adminName, password, "ADMIN");
    }
}

