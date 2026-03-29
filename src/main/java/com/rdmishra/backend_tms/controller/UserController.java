package com.rdmishra.backend_tms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.rdmishra.backend_tms.dto.request.UserRequest;
import com.rdmishra.backend_tms.dto.response.UserResponse;
import com.rdmishra.backend_tms.model.User;
import com.rdmishra.backend_tms.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication auth) {
        String userId = ((User) auth.getPrincipal()).getId().toString();
        return userService.getUserProfile(userId);
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@RequestBody UserResponse request, Authentication auth) {
        String userId = ((User) auth.getPrincipal()).getId().toString();
        return userService.updateSelf(userId, request);
    }

    @DeleteMapping("/me")
    public String deleteMyProfile(Authentication auth) {
        String userId = ((User) auth.getPrincipal()).getId().toString();
        return userService.deleteSelf(userId);
    }

    @PostMapping
    public ResponseEntity<UserResponse> saveUser(@RequestBody UserRequest user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<Iterable<UserResponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserResponse> getUserbyId(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserResponse user) {
        return ResponseEntity.ok(userService.updateUser(userId, user));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deletUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @PostMapping("/upload-image/{userId}")
    public ResponseEntity<UserResponse> uploadImageData(
            @RequestParam("image") MultipartFile file,
            @PathVariable String userId) {
        return ResponseEntity.ok(userService.uplaodImgae(file, userId));
    }
}