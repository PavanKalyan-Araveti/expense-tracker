package com.pavan.expensetracker.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
