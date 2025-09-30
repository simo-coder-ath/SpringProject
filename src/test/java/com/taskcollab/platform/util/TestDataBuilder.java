









package com.taskcollab.platform.util;















import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.Task;
import com.taskcollab.platform.model.TaskStatus;
import com.taskcollab.platform.model.User;











public class TestDataBuilder {
    public static User createUser(Long id, String email, String username) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword("password");
        return user;
    }
    public static Project createProject(Long id, String name, User owner) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription("Test Description");
        project.setOwner(owner);
        return project;
    }





    public static Task createTask(Long id, String title, Project project) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Task Description");
        task.setProject(project);
        task.setStatus(TaskStatus.TODO);
        return task;
    }
}