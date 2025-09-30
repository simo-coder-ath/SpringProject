



package com.taskcollab.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.User;
import com.taskcollab.platform.repository.ProjectRepository;
import com.taskcollab.platform.repository.UserRepository;
import com.taskcollab.platform.util.JwtUtil;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        projectRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setUsername("testuser");
        userRepository.save(testUser);

        jwtToken = jwtUtil.generateToken(testUser.getEmail());
    }

    @Test
    void createProject_ShouldReturnProject_WhenAuthenticated() throws Exception {
       



        Project project = new Project();
        project.setName("New Project");
        project.setDescription("Project Description");

       
        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Project"))
                .andExpect(jsonPath("$.description").value("Project Description"));
    }

    @Test
    void getAllProjects_ShouldReturnProjects_WhenAuthenticated() throws Exception {
       


        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOwner(testUser);
        projectRepository.save(project);

       
        mockMvc.perform(get("/api/projects")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void createProject_ShouldReturnUnauthorized_WhenNoToken() throws Exception {
        



        Project project = new Project();
        project.setName("New Project");
        project.setDescription("Project Description");

        





        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isForbidden());
    }
}