package com.rdmishra.backend_tms.service;

import java.util.Set;

import com.rdmishra.backend_tms.dto.TaskStatus;
import com.rdmishra.backend_tms.dto.request.TaskRequest;
import com.rdmishra.backend_tms.dto.response.TaskResponse;

public interface TaskService {

    // User-specific
    TaskResponse createTask(TaskRequest request, String userId);

    Set<TaskResponse> getAllTasksByUserId(String userId);

    TaskResponse updateTask(String taskId, TaskRequest request, String userId);

    String deleteTask(String taskId, String userId, String role);

    // Admin
    Iterable<TaskResponse> getAllTasks();

    TaskResponse getTaskById(String taskId);

    TaskResponse updateStatus(String taskId, TaskStatus status);
}