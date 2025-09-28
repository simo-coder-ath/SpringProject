package com.taskcollab.platform.controller;

import com.taskcollab.platform.model.Task;
import com.taskcollab.platform.model.TaskStatus;
import com.taskcollab.platform.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.getTasksForUser(email));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        return taskService.getTaskById(id, email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/project/{projectId}")
    public ResponseEntity<Task> createTask(@RequestBody Task task, @PathVariable Long projectId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.createTask(task, projectId, email));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.updateTask(id, taskDetails, email));
    }
    
    @PutMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assignTask(@PathVariable Long taskId, @PathVariable Long userId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.assignTask(taskId, userId, email));
    }
    
    // NOUVEAU ENDPOINT - Assignation réservée au propriétaire
    @PutMapping("/{taskId}/assign-member/{memberId}")
    public ResponseEntity<Task> assignTaskToMember(
            @PathVariable Long taskId, 
            @PathVariable Long memberId,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.assignTaskToMember(taskId, memberId, email));
    }
    
    // NOUVEAU ENDPOINT - Désassignation réservée au propriétaire
    @PutMapping("/{taskId}/unassign")
    public ResponseEntity<Task> unassignTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.unassignTask(taskId, email));
    }
    
    @PutMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatus status, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status, email));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        taskService.deleteTask(id, email);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, email));
    }
    
    @GetMapping("/assignee/{userId}")
    public ResponseEntity<List<Task>> getTasksByAssignee(@PathVariable Long userId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.getTasksByAssignee(userId, email));
    }
    
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<Task>> getTasksByProjectAndStatus(
            @PathVariable Long projectId, 
            @PathVariable TaskStatus status,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.getTasksByProjectAndStatus(projectId, status, email));
    }
}