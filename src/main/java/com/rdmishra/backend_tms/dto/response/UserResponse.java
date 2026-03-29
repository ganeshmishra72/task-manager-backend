package com.rdmishra.backend_tms.dto.response;

import java.util.UUID;

import com.rdmishra.backend_tms.dto.UserRole;
import com.rdmishra.backend_tms.model.Provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String image;

    private UserRole role;

    private boolean enable;

    private Provider provider;
}
