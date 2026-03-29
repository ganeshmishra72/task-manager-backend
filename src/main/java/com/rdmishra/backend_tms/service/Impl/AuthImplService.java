package com.rdmishra.backend_tms.service.Impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rdmishra.backend_tms.dto.request.SingupRequest;
import com.rdmishra.backend_tms.dto.request.UserRequest;
import com.rdmishra.backend_tms.dto.response.UserResponse;
import com.rdmishra.backend_tms.model.User;
import com.rdmishra.backend_tms.service.AuthService;
import com.rdmishra.backend_tms.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthImplService implements AuthService {

    private final PasswordEncoder encoder;
    private final UserService service;
    private final ModelMapper mapper;

    @Override
    public UserResponse registerUser(SingupRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .build();
        UserRequest requestData = mapper.map(user, UserRequest.class);
        return service.createUser(requestData);
    }

}
