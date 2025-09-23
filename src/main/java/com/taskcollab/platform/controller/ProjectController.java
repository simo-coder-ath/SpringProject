package com.taskcollab.platform.controller;

import com.taskcollab.platform.dto.ProjectInvitationRequest;
import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional; 
import org.springframework.http.HttpStatus; 

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    // CRUD Complet
    
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project, Authentication authentication) {
        String email = authentication.getName();
        Project createdProject = projectService.createProject(project, email);
        return ResponseEntity.ok(createdProject);
    }
    
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectsForUser(email));
    }
    
  @GetMapping("/{id}")
public ResponseEntity<Project> getProjectById(@PathVariable Long id, Authentication authentication) {
    String email = authentication.getName();
    Optional<Project> project = projectService.getProjectById(id, email);
    
    if (project.isPresent()) {
        return ResponseEntity.ok(project.get());
    } else {
        // Retourner 403 au lieu de 404 si l'utilisateur n'a pas accès
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id, 
            @RequestBody Project projectDetails, 
            Authentication authentication) {
        String email = authentication.getName();
        Project updatedProject = projectService.updateProject(id, projectDetails, email);
        return ResponseEntity.ok(updatedProject);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        projectService.deleteProject(id, email);
        return ResponseEntity.ok().build();
    }
    
    // Gestion des membres
    
    @PostMapping("/{projectId}/invite")
    public ResponseEntity<Project> inviteMember(
            @PathVariable Long projectId,
            @RequestBody ProjectInvitationRequest invitation,
            Authentication authentication) {
        String email = authentication.getName();
        Project updatedProject = projectService.inviteMemberByEmail(projectId, invitation.getEmail(), email);
        return ResponseEntity.ok(updatedProject);
    }
    
    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Project> addMemberToProject(
            @PathVariable Long projectId, 
            @PathVariable Long userId,
            Authentication authentication) {
        String email = authentication.getName();
        Project updatedProject = projectService.addMemberToProject(projectId, userId, email);
        return ResponseEntity.ok(updatedProject);
    }
    
    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Project> removeMemberFromProject(
            @PathVariable Long projectId, 
            @PathVariable Long userId,
            Authentication authentication) {
        String email = authentication.getName();
        Project updatedProject = projectService.removeMemberFromProject(projectId, userId, email);
        return ResponseEntity.ok(updatedProject);
    }
    
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<com.taskcollab.platform.model.User>> getProjectMembers(
            @PathVariable Long projectId,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectMembers(projectId, email));
    }
    
    // API enrichie
    
    @GetMapping("/my-projects")
    public ResponseEntity<List<Project>> getMyProjects(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectsByOwner(email));
    }
    
    @GetMapping("/member-projects")
    public ResponseEntity<List<Project>> getProjectsWhereMember(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectsByMember(email));
    }
    
    @GetMapping("/{projectId}/is-member")
    public ResponseEntity<Boolean> isUserMemberOfProject(
            @PathVariable Long projectId,
            Authentication authentication) {
        String email = authentication.getName();
        boolean isMember = projectService.isUserMemberOfProject(projectId, email);
        return ResponseEntity.ok(isMember);
    }
    
    @GetMapping("/{projectId}/is-owner")
    public ResponseEntity<Boolean> isUserOwnerOfProject(
            @PathVariable Long projectId,
            Authentication authentication) {
        String email = authentication.getName();
        boolean isOwner = projectService.isUserOwnerOfProject(projectId, email);
        return ResponseEntity.ok(isOwner);
    }
}