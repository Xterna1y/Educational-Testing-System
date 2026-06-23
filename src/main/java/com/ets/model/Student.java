package com.ets.model;

/**
 * Represents a student user in the Educational Testing System.
 * <p>
 * A {@code Student} is a specialised {@link User} with the {@code "STUDENT"} role,
 * allowing them to take exams and view their results. Students do not have
 * access to administrative functionality.
 * </p>
 *
 * @author ETS Team
 * @version 1.0
 * @see User
 */
public class Student extends User {

    /**
     * Constructs a new {@code Student} with the given credentials.
     * <p>
     * The role is automatically set to {@code "STUDENT"} regardless of the value
     * passed for the {@code role} parameter.
     * </p>
     *
     * @param studentName the login name for this student
     * @param password    the password for this student
     * @param role        ignored; the role is always set to {@code "STUDENT"}
     */
    public Student(String studentName, String password, String role) {
        super(studentName, password, "STUDENT");
    }
}

