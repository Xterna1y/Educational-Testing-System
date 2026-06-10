package com.ets.model;

public class Student extends User {
    public Student(String studentName, String password, String role) {
        super(studentName, password, "STUDENT");
    }
}
