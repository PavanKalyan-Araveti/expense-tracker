package com.pavan.expensetracker.service.impl;

import com.pavan.expensetracker.exception.DuplicateUserException;
import com.pavan.expensetracker.exception.InvalidCredentialsException;
import com.pavan.expensetracker.exception.UserNotFoundException;
import com.pavan.expensetracker.model.User;
import com.pavan.expensetracker.repository.UserRepository;
import com.pavan.expensetracker.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(String userName, String rawPassword, String fullName) {
        if(userRepository.existsByUserName(userName)){
            throw new DuplicateUserException("Username already exists: "+ userName);
        }
        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = User.builder()
                .userName(userName)
                .passwordHash(hashedPassword)
                .fullName(fullName)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String rawPassword) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));
        boolean matches = passwordEncoder.matches(rawPassword, user.getPasswordHash());
        if(!matches) throw new InvalidCredentialsException("Invalid username or password");
        return user;
    }
}
