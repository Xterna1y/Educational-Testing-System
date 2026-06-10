package com.ets.model;

public class Admin extends User {
    public Admin(String adminName, String password, String role) {
        super(adminName, password, "ADMIN");
    }
}
