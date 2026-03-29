package com.rdmishra.backend_tms.dto.response;

import java.util.UUID;

import com.rdmishra.backend_tms.dto.TaskStatus;

import lombok.Data;

@Data
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private UUID userId;
}
