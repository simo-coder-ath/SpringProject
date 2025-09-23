package com.taskcollab.platform.repository;

import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.Task;
import com.taskcollab.platform.model.TaskStatus;
import com.taskcollab.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignee(User assignee);
    List<Task> findByProjectAndStatus(Project project, TaskStatus status);
}