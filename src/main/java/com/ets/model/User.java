package com.ets.model;

/**
 * Abstract base class representing a user in the Educational Testing System.
 * <p>
 * This class defines the common properties and behaviour shared by all user
 * types (e.g. {@link Admin} and {@link Student}). It stores authentication
 * credentials and a role identifier that determines which parts of the system
 * a particular user may access.
 * </p>
 *
 * @author ETS Team
 * @version 1.0
 */
public abstract class User {
    protected String username;
    protected String password;
    protected String role;

    /**
     * Constructs a new {@code User} with the specified credentials and role.
     *
     * @param username the unique login name of the user
     * @param password the password used to authenticate the user
     * @param role     a string constant identifying the user's role (e.g. {@code "ADMIN"} or {@code "STUDENT"})
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Returns the username of this user.
     *
     * @return the username
     */
    public String getUsername() {return username;}

    /**
     * Returns the password of this user.
     *
     * @return the password
     */
    public String getPassword() {return password;}

    /**
     * Returns the role assigned to this user.
     *
     * @return the role string (e.g. {@code "ADMIN"} or {@code "STUDENT"})
     */
    public String getRole() {return role;}
}
