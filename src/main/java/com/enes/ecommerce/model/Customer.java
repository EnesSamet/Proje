package com.enes.ecommerce.model;

public class Customer extends User {
    public Customer(int id, String username, String password) {
        super(id, username, password, "CUSTOMER");
    }
}
