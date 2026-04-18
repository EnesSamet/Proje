package com.enes.ecommerce.service;

import com.enes.ecommerce.data.UserDao;
import com.enes.ecommerce.model.User;

public class AuthService {
    private UserDao userDao;
    private User currentUser;

    public AuthService() {
        this.userDao = new UserDao();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new Exception("Kullanıcı adı ve şifre boş bırakılamaz.");
        }

        User user = userDao.getByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        } else {
            throw new Exception("Hatalı kullanıcı adı veya şifre.");
        }
    }

    public void logout() {
        this.currentUser = null;
    }
}
