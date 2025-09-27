// src/main/java/com/taskcollab/platform/model/Invitation.java
package com.taskcollab.platform.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;
    
    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;
    
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    
    // Constructeurs
    public Invitation() {
        this.createdAt = LocalDateTime.now();
        this.status = InvitationStatus.PENDING;
    }
    
    public Invitation(Project project, String email, User inviter) {
        this();
        this.project = project;
        this.email = email;
        this.inviter = inviter;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }
    
    public User getInviter() { return inviter; }
    public void setInviter(User inviter) { this.inviter = inviter; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}