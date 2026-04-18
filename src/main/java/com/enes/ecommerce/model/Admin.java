package com.enes.ecommerce.model;

public class Admin extends User {
    public Admin(int id, String username, String password) {
        super(id, username, password, "ADMIN");
    }
}
