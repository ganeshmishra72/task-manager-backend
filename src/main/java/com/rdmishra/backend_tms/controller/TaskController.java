package com.rdmishra.backend_tms.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.rdmishra.backend_tms.dto.TaskStatus;
import com.rdmishra.backend_tms.dto.request.TaskRequest;
import com.rdmishra.backend_tms.dto.response.TaskResponse;
import com.rdmishra.backend_tms.model.User;
import com.rdmishra.backend_tms.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/my")
    public Set<TaskResponse> getMyTasks(Authentication auth) {
        String userId = ((User) auth.getPrincipal()).getId().toString();
        return taskService.getAllTasksByUserId(userId);
    }

    @PostMapping("/my")
    public TaskResponse createTask(@RequestBody TaskRequest request, Authentication auth) {
        String userId = ((User) auth.getPrincipal()).getId().toString();
        return taskService.createTask(request, userId);
    }

    @PutMapping("/my/{id}")
    public TaskResponse updateTask(@PathVariable String id, @RequestBody TaskRequest request, Authentication auth) {
        String userId = ((User) auth.getPrincipal()).getId().toString();
        return taskService.updateTask(id, request, userId);
    }

    @DeleteMapping("/my/{id}")
    public String deleteTask(@PathVariable String id, Authentication auth) {
        String userId = ((User) auth.getPrincipal()).getId().toString();
        String role = ((User) auth.getPrincipal()).getRole().toString();
        return taskService.deleteTask(id, userId, role);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<TaskResponse>> getAllTask() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/id/{taskId}")
    public ResponseEntity<TaskResponse> getTaskbyId(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @PutMapping("/updateStatus/{taskId}")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable String taskId,
            @RequestBody TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, status));
    }
}