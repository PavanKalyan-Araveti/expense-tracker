package com.pavan.expensetracker.service;

import com.pavan.expensetracker.exception.DuplicateUserException;
import com.pavan.expensetracker.exception.InvalidCredentialsException;
import com.pavan.expensetracker.model.User;
import com.pavan.expensetracker.repository.UserRepository;
import com.pavan.expensetracker.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp(){
        mockUser = User.builder()
                .id(1L)
                .userName("Pavan")
                .passwordHash("hashed123")
                .fullName("PavanKalyan")
                .build();
    }

    @Test
    void contextLoads(){
        assertNotNull(userService);
    }

    @Test
    void registerUser_success(){
        String rawPassword = "P@ssw0rd";
        when(repo.existsByUserName("pavan")).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn("hashed123");
        when(repo.save(any())).thenAnswer(Invocation -> {
            User toSave = Invocation.getArgument(0);
            toSave.setId(1L);
            return toSave;
        });

        User saved = userService.registerUser("pavan", rawPassword, "Pavan Kalyan");
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("pavan", saved.getUserName());
        assertEquals("hashed123", saved.getPasswordHash());
        assertEquals("Pavan Kalyan", saved.getFullName());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_duplicate(){
        String userName = "alice";
        String password = "password123";
        String fullName = "Alice Bob";

        when(repo.existsByUserName(userName)).thenReturn(true);
        assertThrows(DuplicateUserException.class, () -> userService.registerUser(userName, password, fullName));
        verify(repo).existsByUserName(userName);
        verify(repo, never()).save(any(User.class));
        verifyNoMoreInteractions(repo);
    }


    @Test
    void login_success(){
        String userName = "alice";
        String rawPassword = "password123";
        String storedHash = "fhbfehurv@#$5bvfhjfkfvknvmfk";
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUserName(userName);
        existingUser.setPasswordHash(storedHash);

        when(repo.findByUserName(userName)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(rawPassword, storedHash)).thenReturn(true);

        User result = userService.login(userName, rawPassword);
        assertNotNull(result, "Expected No null user on successful login");
        verify(repo).findByUserName(userName);
        verify(passwordEncoder).matches(rawPassword, storedHash);
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void login_invalidPassword(){
      String userName = "alice";
      String rawPassword = "password123";
      String storedHash = "4tgv32fvrvf";
      User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUserName(userName);
        existingUser.setPasswordHash(storedHash);

        when(repo.findByUserName(userName)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(rawPassword, storedHash)).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> userService.login(userName, rawPassword));
        verify(repo).findByUserName(userName);
        verify(passwordEncoder).matches(rawPassword, storedHash);
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void login_userNotFound(){
        String userName = "alice";
        String rawPassword = "password123";
        when(repo.findByUserName(userName)).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> userService.login(userName, rawPassword));
        verify(repo).findByUserName(userName);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verifyNoMoreInteractions(repo, passwordEncoder);
    }
}
