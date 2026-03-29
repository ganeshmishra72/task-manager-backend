package com.rdmishra.backend_tms.repo;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rdmishra.backend_tms.model.Task;

public interface TaskRepo extends JpaRepository<Task, UUID> {

    Set<Task> findByUserId(UUID userId);
}
