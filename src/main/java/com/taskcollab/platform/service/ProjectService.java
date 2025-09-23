package com.taskcollab.platform.service;

import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.User;
import com.taskcollab.platform.repository.ProjectRepository;
import com.taskcollab.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    
    public Project createProject(Project project, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow();
        project.setOwner(owner);
        return projectRepository.save(project);
    }
    
    public Project addMemberToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        project.getMembers().add(user);
        return projectRepository.save(project);
    }
    
    public List<Project> getProjectsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return projectRepository.findByMembersContaining(user);
    }
}