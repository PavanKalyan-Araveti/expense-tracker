package com.pavan.expensetracker.service;

import com.pavan.expensetracker.model.User;

public interface UserService {
    User registerUser(String userName, String rawPassword, String fullName);
    User login(String username, String rawPassword);
}
