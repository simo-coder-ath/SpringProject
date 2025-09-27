package com.taskcollab.platform.repository;

import com.taskcollab.platform.model.Invitation;
import com.taskcollab.platform.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByEmailAndStatus(String email, InvitationStatus status);
    List<Invitation> findByProjectIdAndStatus(Long projectId, InvitationStatus status);
    Optional<Invitation> findByIdAndEmail(Long id, String email);
    boolean existsByProjectIdAndEmailAndStatus(Long projectId, String email, InvitationStatus status);
}