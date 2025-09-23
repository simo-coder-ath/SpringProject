package com.taskcollab.platform.service;

import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.User;
import com.taskcollab.platform.repository.ProjectRepository;
import com.taskcollab.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // CRUD Operations
    
    public Project createProject(Project project, String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);
        project.setOwner(owner);
        project.addMember(owner); // L'owner est automatiquement membre
        return projectRepository.save(project);
    }
    
  public List<Project> getProjectsForUser(String email) {
    try {
        User user = getUserByEmail(email);
        List<Project> ownedProjects = projectRepository.findByOwner(user);
        List<Project> memberProjects = projectRepository.findByMembersContaining(user);
        
        List<Project> allProjects = new ArrayList<>(ownedProjects);
        for (Project project : memberProjects) {
            if (!containsProject(allProjects, project)) {
                allProjects.add(project);
            }
        }
        return allProjects;
    } catch (Exception e) {
        System.err.println("Error in getProjectsForUser: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    }
}

    
   public Optional<Project> getProjectById(Long id, String email) {
    try {
        User user = getUserByEmail(email);
        Optional<Project> projectOpt = projectRepository.findById(id);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            // Vérifier si l'utilisateur a accès au projet (owner ou membre)
            if (isUserOwnerOrMember(project, user)) {
                return Optional.of(project);
            } else {
                // L'utilisateur n'a pas accès, retourner empty sans erreur
                return Optional.empty();
            }
        }
        return Optional.empty();
    } catch (Exception e) {
        // Logger l'erreur et retourner empty
        System.err.println("Error in getProjectById: " + e.getMessage());
        return Optional.empty();
    }
}
    
    public Project updateProject(Long id, Project projectDetails, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Seul l'owner peut modifier le projet
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Only project owner can update the project");
        }
        
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        
        return projectRepository.save(project);
    }
    
    public void deleteProject(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Seul l'owner peut supprimer le projet
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Only project owner can delete the project");
        }
        
        // Les cascades vont supprimer automatiquement les tâches et relations membres
        projectRepository.delete(project);
    }
    
    // Gestion des membres
    
    public Project inviteMemberByEmail(Long projectId, String memberEmail, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Seul l'owner ou un membre peut inviter d'autres membres
        if (!isUserOwnerOrMember(project, requester)) {
            throw new RuntimeException("You don't have permission to invite members to this project");
        }
        
        User userToInvite = userRepository.findByEmail(memberEmail)
            .orElseThrow(() -> new RuntimeException("User with email " + memberEmail + " not found"));
        
        return addMemberToProject(projectId, userToInvite.getId(), requesterEmail);
    }
    
    public Project addMemberToProject(Long projectId, Long userId, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Seul l'owner ou un membre peut ajouter des membres
        if (!isUserOwnerOrMember(project, requester)) {
            throw new RuntimeException("You don't have permission to add members to this project");
        }
        
        User userToAdd = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Vérifier si l'utilisateur est déjà membre
        if (project.isMember(userToAdd)) {
            throw new RuntimeException("User is already a member of this project");
        }
        
        project.addMember(userToAdd);
        return projectRepository.save(project);
    }
    
    public Project removeMemberFromProject(Long projectId, Long userId, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        User userToRemove = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Seul l'owner peut retirer des membres (et un membre peut se retirer lui-même)
        if (!project.getOwner().getId().equals(requester.getId()) && 
            !requester.getId().equals(userToRemove.getId())) {
            throw new RuntimeException("Only project owner can remove other members");
        }
        
        // L'owner ne peut pas se retirer lui-même
        if (project.getOwner().getId().equals(userToRemove.getId())) {
            throw new RuntimeException("Project owner cannot be removed from the project");
        }
        
        project.removeMember(userToRemove);
        return projectRepository.save(project);
    }
    
    public List<User> getProjectMembers(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!isUserOwnerOrMember(project, user)) {
            throw new RuntimeException("You don't have access to this project");
        }
        
        List<User> members = new ArrayList<>(project.getMembers());
        members.add(project.getOwner()); // Inclure l'owner dans la liste des membres
        return members;
    }
    
    // Méthodes utilitaires
    
   public List<Project> getProjectsByOwner(String email) {
    try {
        User user = getUserByEmail(email);
        return projectRepository.findByOwner(user);
    } catch (Exception e) {
        System.err.println("Error in getProjectsByOwner: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    }
}
    
   public List<Project> getProjectsByMember(String email) {
    try {
        User user = getUserByEmail(email);
        return projectRepository.findByMembersContaining(user);
    } catch (Exception e) {
        System.err.println("Error in getProjectsByMember: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>(); // Retourner une liste vide au lieu de planter
    }
}
    
    public boolean isUserMemberOfProject(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return isUserOwnerOrMember(project, user);
    }
    
    public boolean isUserOwnerOfProject(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return project.getOwner().getId().equals(user.getId());
    }
    
    // Méthodes privées
    
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private boolean isUserOwnerOrMember(Project project, User user) {
        return project.getOwner().getId().equals(user.getId()) || 
               project.getMembers().stream().anyMatch(member -> member.getId().equals(user.getId()));
    }
    
    private boolean containsProject(List<Project> projects, Project project) {
        return projects.stream().anyMatch(p -> p.getId().equals(project.getId()));
    }
}