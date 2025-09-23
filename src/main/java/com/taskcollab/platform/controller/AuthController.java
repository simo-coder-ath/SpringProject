package com.taskcollab.platform.controller;

import com.taskcollab.platform.dto.AuthRequest;
import com.taskcollab.platform.dto.AuthResponse;
import com.taskcollab.platform.model.User;
import com.taskcollab.platform.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = authService.registerUser(user);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            String token = authService.loginUser(authRequest.getEmail(), authRequest.getPassword());
            User user = authService.getUserByEmail(authRequest.getEmail());
            AuthResponse response = new AuthResponse(token, user.getEmail(), user.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}