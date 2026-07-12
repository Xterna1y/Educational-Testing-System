package com.ets.model;

/**
 * Represents a student user in the Educational Testing System.
 * <p>
 * A {@code Student} is a specialised {@link User} whose role is always
 * {@code "STUDENT"}, allowing them to take quizzes and view their results.
 * </p>
 *
 * @author ETS Team
 * @version 1.0
 * @see User
 */
public class Student extends User {

    /**
     * Constructs a new {@code Student} with the given login name.
     * The role is always set to {@code "STUDENT"}.
     *
     * @param studentName the login name for this student
     */
    public Student(String studentName) {
        super(studentName, "STUDENT");
    }
}