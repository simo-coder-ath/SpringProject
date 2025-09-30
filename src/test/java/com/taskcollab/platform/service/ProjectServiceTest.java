
package com.taskcollab.platform.service;








import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.User;
import com.taskcollab.platform.repository.ProjectRepository;
import com.taskcollab.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;








@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("owner@example.com");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setOwner(testUser);
    }

    @Test
    void createProject_ShouldSuccess_WhenValidData() {
      
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

       
        Project result = projectService.createProject(testProject, "owner@example.com");

       
        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void getProjectsForUser_ShouldReturnProjects_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepository.findByOwner(any(User.class))).thenReturn(Arrays.asList(testProject));
        when(projectRepository.findByMembersContaining(any(User.class))).thenReturn(Arrays.asList());

      
        List<Project> result = projectService.getProjectsForUser("owner@example.com");

      
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
    }

    @Test
    void getProjectById_ShouldReturnProject_WhenUserHasAccess() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));

       
        Optional<Project> result = projectService.getProjectById(1L, "owner@example.com");

      
        assertTrue(result.isPresent());
        assertEquals("Test Project", result.get().getName());
    }

    @Test
    void updateProject_ShouldSuccess_WhenUserIsOwner() {
       
        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");
        updatedProject.setDescription("Updated Description");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

     





        Project result = projectService.updateProject(1L, updatedProject, "owner@example.com");

        
        assertNotNull(result);
        assertEquals("Updated Project", result.getName());
    }
}