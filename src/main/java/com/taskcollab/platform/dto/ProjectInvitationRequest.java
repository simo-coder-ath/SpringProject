package com.taskcollab.platform.dto;

public class ProjectInvitationRequest {
    private String email;
    private String message;
    
    // Constructeurs
    public ProjectInvitationRequest() {}
    
    public ProjectInvitationRequest(String email, String message) {
        this.email = email;
        this.message = message;
    }
    
    // Getters et Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}