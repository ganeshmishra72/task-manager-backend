package com.rdmishra.backend_tms.service.Impl;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.rdmishra.backend_tms.dto.TaskStatus;
import com.rdmishra.backend_tms.dto.request.TaskRequest;
import com.rdmishra.backend_tms.dto.response.TaskResponse;
import com.rdmishra.backend_tms.exception.ResourceNotFoundException;
import com.rdmishra.backend_tms.helper.UserHelper;
import com.rdmishra.backend_tms.model.Task;
import com.rdmishra.backend_tms.repo.TaskRepo;
import com.rdmishra.backend_tms.service.TaskService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskImplService implements TaskService {

    private final TaskRepo taskRepo;
    private final ModelMapper mapper;

    @Override
    public Iterable<TaskResponse> getAllTasks() {
        return taskRepo.findAll().stream().map(task -> mapper.map(task, TaskResponse.class)).toList();

    }

    @Override
    public TaskResponse getTaskById(String taskId) {
        UUID utaskId = UserHelper.parseUUId(taskId);
        Task task = taskRepo.findById(utaskId).orElseThrow(() -> new ResourceNotFoundException("Task Not found"));
        return mapper.map(task, TaskResponse.class);
    }

    @Override
    public TaskResponse updateStatus(String taskId, TaskStatus status) {
        Task task = taskRepo.findById(UserHelper.parseUUId(taskId))
                .orElseThrow(() -> new ResourceNotFoundException("Task Not found"));
        task.setStatus(status);
        Task saveTask = taskRepo.save(task);
        return mapper.map(saveTask, TaskResponse.class);
    }

    @Override
    public TaskResponse createTask(TaskRequest request, String userId) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .userId(UUID.fromString(userId))
                .build();
        return mapper.map(taskRepo.save(task), TaskResponse.class);
    }

    @Override
    public TaskResponse updateTask(String taskId, TaskRequest request, String userId) {
        Task task = taskRepo.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));
        if (!task.getUserId().toString().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        return mapper.map(taskRepo.save(task), TaskResponse.class);
    }

    @Override
    public String deleteTask(String taskId, String userId, String role) {
        Task task = taskRepo.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));

        // Allow deletion if admin or owner
        if (!task.getUserId().toString().equals(userId) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Unauthorized");
        }

        taskRepo.delete(task);
        return "Deleted Task Successfully";
    }

    @Override
    public Set<TaskResponse> getAllTasksByUserId(String userId) {
        try {

            UUID uuserId = UserHelper.parseUUId(userId);
            return taskRepo.findByUserId(uuserId).stream().map(task -> mapper.map(task, TaskResponse.class))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            // TODO: handle exception
            throw new ResourceNotFoundException("User not Found");
        }
    }

}
