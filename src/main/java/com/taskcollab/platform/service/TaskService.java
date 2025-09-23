package com.taskcollab.platform.service;

import com.taskcollab.platform.model.Task;
import com.taskcollab.platform.model.TaskStatus;
import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.User;
import com.taskcollab.platform.repository.TaskRepository;
import com.taskcollab.platform.repository.ProjectRepository;
import com.taskcollab.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<Task> getTasksForUser(String email) {
        User user = getUserByEmail(email);
        // Retourne les tâches où l'utilisateur est assigné ou fait partie du projet
        List<Task> assignedTasks = taskRepository.findByAssignee(user);
        List<Task> projectTasks = getTasksFromUserProjects(user);
        
        // Combiner les listes et supprimer les doublons
        List<Task> allTasks = new ArrayList<>(assignedTasks);
        for (Task task : projectTasks) {
            if (!containsTask(allTasks, task)) {
                allTasks.add(task);
            }
        }
        return allTasks;
    }
    
    public Optional<Task> getTaskById(Long id, String email) {
        User user = getUserByEmail(email);
        Optional<Task> taskOpt = taskRepository.findById(id);
        
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            // Vérifier si l'utilisateur a accès à la tâche
            if (hasAccessToTask(task, user)) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }
    
    public Task createTask(Task task, Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Vérifier que l'utilisateur peut créer des tâches dans ce projet
        if (!isUserOwnerOrMember(project, user)) {
            throw new RuntimeException("You don't have permission to create tasks in this project");
        }
        
        task.setProject(project);
        return taskRepository.save(task);
    }
    
    public Task updateTask(Long id, Task taskDetails, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Vérifier les permissions
        if (!hasAccessToTask(task, user)) {
            throw new RuntimeException("You don't have permission to update this task");
        }
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());
        
        return taskRepository.save(task);
    }
    
    public Task assignTask(Long taskId, Long userId, String email) {
        User requester = getUserByEmail(email);
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Vérifier que le requester peut assigner des tâches dans ce projet
        Project project = task.getProject();
        if (!isUserOwnerOrMember(project, requester)) {
            throw new RuntimeException("You don't have permission to assign tasks in this project");
        }
        
        User userToAssign = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        task.setAssignee(userToAssign);
        return taskRepository.save(task);
    }
    
    public Task updateTaskStatus(Long taskId, TaskStatus status, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Seul l'assigné ou le propriétaire/membre du projet peut changer le statut
        if (!hasAccessToTask(task, user)) {
            throw new RuntimeException("You don't have permission to update this task's status");
        }
        
        task.setStatus(status);
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long id, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Seul le propriétaire du projet peut supprimer la tâche
        Project project = task.getProject();
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Only project owner can delete tasks");
        }
        
        taskRepository.deleteById(id);
    }
    
    public List<Task> getTasksByProject(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Vérifier l'accès au projet
        if (!isUserOwnerOrMember(project, user)) {
            throw new RuntimeException("You don't have access to this project");
        }
        
        return taskRepository.findByProject(project);
    }
    
    public List<Task> getTasksByAssignee(Long userId, String email) {
        User requester = getUserByEmail(email);
        User assignee = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Un utilisateur ne peut voir que ses propres tâches assignées
        // sauf s'il a des permissions spéciales
        if (!requester.getId().equals(assignee.getId())) {
            // Vérifier si le requester a des droits étendus
            if (!hasExtendedPermissions(requester)) {
                throw new RuntimeException("You can only view your own assigned tasks");
            }
        }
        
        return taskRepository.findByAssignee(assignee);
    }
    
    public List<Task> getTasksByProjectAndStatus(Long projectId, TaskStatus status, String email) {
        User user = getUserByEmail(email);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!isUserOwnerOrMember(project, user)) {
            throw new RuntimeException("You don't have access to this project");
        }
        
        return taskRepository.findByProjectAndStatus(project, status);
    }
    
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private boolean hasAccessToTask(Task task, User user) {
        Project project = task.getProject();
        return isUserOwnerOrMember(project, user) || 
               (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId()));
    }
    
    private boolean isUserOwnerOrMember(Project project, User user) {
        return project.getOwner().getId().equals(user.getId()) || 
               project.getMembers().stream().anyMatch(member -> member.getId().equals(user.getId()));
    }
    
    private boolean hasExtendedPermissions(User user) {
        // Ici vous pourriez vérifier des rôles spécifiques
        // Pour l'instant, retourne true si l'utilisateur est propriétaire d'au moins un projet
        return !projectRepository.findByOwner(user).isEmpty();
    }
    
    private List<Task> getTasksFromUserProjects(User user) {
        List<Project> userProjects = projectRepository.findByMembersContaining(user);
        userProjects.addAll(projectRepository.findByOwner(user));
        
        List<Task> allTasks = new ArrayList<>();
        for (Project project : userProjects) {
            List<Task> projectTasks = taskRepository.findByProject(project);
            for (Task task : projectTasks) {
                if (!containsTask(allTasks, task)) {
                    allTasks.add(task);
                }
            }
        }
        return allTasks;
    }
    // Ajoutez cette méthode pour vérifier les permissions des tâches
private boolean canUserModifyTask(Task task, User user) {
    Project project = task.getProject();
    
    // L'owner du projet peut tout modifier
    if (project.getOwner().getId().equals(user.getId())) {
        return true;
    }
    
    // L'assigné de la tâche peut modifier son statut
    if (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) {
        return true;
    }
    
    // Les membres du projet peuvent créer/modifier les tâches (sauf suppression)
    return project.getMembers().stream().anyMatch(member -> member.getId().equals(user.getId()));
}

    
    private boolean containsTask(List<Task> tasks, Task task) {
        return tasks.stream().anyMatch(t -> t.getId().equals(task.getId()));
    }
}