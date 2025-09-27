package com.taskcollab.platform.service;

import com.taskcollab.platform.model.*;
import com.taskcollab.platform.repository.InvitationRepository;
import com.taskcollab.platform.repository.ProjectRepository;
import com.taskcollab.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvitationService {
    
    @Autowired
    private InvitationRepository invitationRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Invitation createInvitation(Long projectId, String email, String inviterEmail) {
        User inviter = getUserByEmail(inviterEmail);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Vérifier si l'utilisateur existe
        Optional<User> invitedUser = userRepository.findByEmail(email);
        if (invitedUser.isEmpty()) {
            throw new RuntimeException("User with email " + email + " not found");
        }
        
        // Vérifier si l'utilisateur est déjà membre
        if (project.isMember(invitedUser.get())) {
            throw new RuntimeException("User is already a member of this project");
        }
        
        // Vérifier s'il y a déjà une invitation en attente
        if (invitationRepository.existsByProjectIdAndEmailAndStatus(projectId, email, InvitationStatus.PENDING)) {
            throw new RuntimeException("Invitation already sent to this user");
        }
        
        // Créer l'invitation
        Invitation invitation = new Invitation(project, email, inviter);
        return invitationRepository.save(invitation);
    }
    
    public List<Invitation> getInvitationsByEmail(String email) {
        return invitationRepository.findByEmailAndStatus(email, InvitationStatus.PENDING);
    }
    
    public Project acceptInvitation(Long invitationId, String email) {
        Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new RuntimeException("Invitation not found"));
        
        // Vérifier que l'invitation est pour cet utilisateur
        if (!invitation.getEmail().equals(email)) {
            throw new RuntimeException("This invitation is not for you");
        }
        
        // Vérifier que l'invitation est en attente
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("This invitation has already been processed");
        }
        
        // Trouver l'utilisateur
        User user = getUserByEmail(email);
        
        // Ajouter l'utilisateur au projet
        Project project = invitation.getProject();
        project.addMember(user);
        projectRepository.save(project);
        
        // Marquer l'invitation comme acceptée
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
        
        return project;
    }
    
    public void declineInvitation(Long invitationId, String email) {
        Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new RuntimeException("Invitation not found"));
        
        if (!invitation.getEmail().equals(email)) {
            throw new RuntimeException("This invitation is not for you");
        }
        
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("This invitation has already been processed");
        }
        
        // Marquer l'invitation comme refusée
        invitation.setStatus(InvitationStatus.DECLINED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
    }
    
    public List<Invitation> getPendingInvitationsForProject(Long projectId, String email) {
        // Vérifier que l'utilisateur a accès au projet
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!project.isMember(user)) {
            throw new RuntimeException("You don't have access to this project");
        }
        
        return invitationRepository.findByProjectIdAndStatus(projectId, InvitationStatus.PENDING);
    }
    
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}