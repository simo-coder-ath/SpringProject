
package com.taskcollab.platform.controller;





import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskcollab.platform.dto.AuthRequest;
import com.taskcollab.platform.model.User;
import com.taskcollab.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;









import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldReturnUser_WhenValidInput() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUsername("testuser");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailExists() throws Exception {
        


        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("password");
        existingUser.setUsername("existinguser");
        userRepository.save(existingUser);

        User newUser = new User();
        newUser.setEmail("existing@example.com");
        newUser.setPassword("password");
        newUser.setUsername("newuser");





        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() throws Exception {
        



        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setUsername("loginuser");
        userRepository.save(user);

        AuthRequest authRequest = new AuthRequest("login@example.com", "password");

        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("login@example.com"));
    }
}