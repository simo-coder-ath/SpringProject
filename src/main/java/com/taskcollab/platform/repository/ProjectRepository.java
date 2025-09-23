package com.taskcollab.platform.repository;

import com.taskcollab.platform.model.Project;
import com.taskcollab.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
    List<Project> findByMembersContaining(User member);
}