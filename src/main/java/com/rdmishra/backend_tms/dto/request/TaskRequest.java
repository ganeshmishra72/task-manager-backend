package com.rdmishra.backend_tms.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class TaskRequest {

    private String title;
    private String description;
    private UUID userId;
}
