package com.taskcollab.platform.controller;

import com.taskcollab.platform.model.Invitation;
import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {
    
    @Autowired
    private InvitationService invitationService;
    
    // Récupérer les invitations de l'utilisateur connecté
    @GetMapping("/my-invitations")
    public ResponseEntity<List<Invitation>> getMyInvitations(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(invitationService.getInvitationsByEmail(email));
    }
    
    // Accepter une invitation
    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Project> acceptInvitation(@PathVariable Long invitationId, Authentication authentication) {
        String email = authentication.getName();
        Project project = invitationService.acceptInvitation(invitationId, email);
        return ResponseEntity.ok(project);
    }
    
    // Refuser une invitation
    @PostMapping("/{invitationId}/decline")
    public ResponseEntity<Void> declineInvitation(@PathVariable Long invitationId, Authentication authentication) {
        String email = authentication.getName();
        invitationService.declineInvitation(invitationId, email);
        return ResponseEntity.ok().build();
    }
    
    // Récupérer les invitations en attente d'un projet (pour le propriétaire)
    @GetMapping("/project/{projectId}/pending")
    public ResponseEntity<List<Invitation>> getPendingInvitationsForProject(
            @PathVariable Long projectId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(invitationService.getPendingInvitationsForProject(projectId, email));
    }
}