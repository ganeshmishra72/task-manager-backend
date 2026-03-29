package com.rdmishra.backend_tms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingupRequest {

    private String username;
    private String email;
    private String password;
}
