package com.taskcollab.platform.service;

import com.taskcollab.platform.model.User;
import com.taskcollab.platform.repository.UserRepository;
import com.taskcollab.platform.util.JwtUtil;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;



    @InjectMocks
    private AuthService authService;


    private User testUser;





    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setUsername("testuser");
    }










    @Test
    void registerUser_ShouldSuccess_WhenValidUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = authService.registerUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
     


        when(userRepository.existsByEmail(anyString())).thenReturn(true);

     
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerUser(testUser));
        assertEquals("Email déjà utilisé", exception.getMessage());
    }

    @Test
    void loginUser_ShouldReturnToken_WhenValidCredentials() {
     
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwtToken");

  
        String token = authService.loginUser("test@example.com", "password");


        assertNotNull(token);
        assertEquals("jwtToken", token);
    }

    @Test
    void loginUser_ShouldThrowException_WhenInvalidCredentials() {
      
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

       
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.loginUser("wrong@example.com", "password"));
        assertEquals("Email ou mot de passe incorrect", exception.getMessage());
    }
}