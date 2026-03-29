package com.rdmishra.backend_tms.service;

import org.springframework.web.multipart.MultipartFile;

import com.rdmishra.backend_tms.dto.request.UserRequest;
import com.rdmishra.backend_tms.dto.response.UserResponse;

public interface UserService {

    // Admin operations
    UserResponse createUser(UserRequest user);

    Iterable<UserResponse> getAllUsers();

    UserResponse getUserById(String userId);

    UserResponse updateUser(String userId, UserResponse user); // admin update

    String deleteUser(String userId);

    UserResponse uplaodImgae(MultipartFile file, String userId);

    // User operations
    UserResponse getUserProfile(String userId);

    UserResponse updateSelf(String userId, UserResponse user);

    String deleteSelf(String userId);
}