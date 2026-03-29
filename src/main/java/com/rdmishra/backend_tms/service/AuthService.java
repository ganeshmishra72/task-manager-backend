package com.rdmishra.backend_tms.service;

import com.rdmishra.backend_tms.dto.request.SingupRequest;
import com.rdmishra.backend_tms.dto.response.UserResponse;

public interface AuthService {
    UserResponse registerUser(SingupRequest request);
}
