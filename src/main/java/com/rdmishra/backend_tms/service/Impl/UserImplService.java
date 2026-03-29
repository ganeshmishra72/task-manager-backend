package com.rdmishra.backend_tms.service.Impl;

import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.rdmishra.backend_tms.dto.request.UserRequest;
import com.rdmishra.backend_tms.dto.response.UserResponse;
import com.rdmishra.backend_tms.exception.ResourceNotFoundException;
import com.rdmishra.backend_tms.helper.UserHelper;
import com.rdmishra.backend_tms.model.User;
import com.rdmishra.backend_tms.repo.UserRepo;
import com.rdmishra.backend_tms.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserImplService implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper mapper;
    private final Cloudinary cloudinary;

    @Override
    public UserResponse createUser(UserRequest user) {
        User userData = mapper.map(user, User.class);
        return mapper.map(userRepo.save(userData), UserResponse.class);
    }

    @Override
    public Iterable<UserResponse> getAllUsers() {
        return userRepo.findAll().stream().map(user -> mapper.map(user, UserResponse.class)).toList();
    }

    @Override
    public UserResponse getUserById(String userId) {
        UUID uuserId = UserHelper.parseUUId(userId);
        User user = userRepo.findById(uuserId).orElseThrow(() -> new ResourceNotFoundException("User Not found"));
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse updateUser(String userId, UserResponse user) {
        User userdata = userRepo.findById(UserHelper.parseUUId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User Not found"));
        userdata.setEmail(user.getEmail());
        userdata.setUsername(user.getUsername());
        userdata.setImage(user.getImage());
        userdata.setRole(user.getRole());
        userdata.setProvider(user.getProvider());

        return mapper.map(userRepo.save(userdata), UserResponse.class);
    }

    @Override
    public String deleteUser(String userId) {
        User userdata = mapper.map(getUserById(userId), User.class);
        userRepo.delete(userdata);

        return "Deleted Successfully";

    }

    @Override
    public UserResponse updateSelf(String currentUserId, UserResponse user) {
        User existing = userRepo.findById(UUID.fromString(currentUserId))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setImage(user.getImage());
        return mapper.map(userRepo.save(existing), UserResponse.class);
    }

    @Override
    public String deleteSelf(String currentUserId) {
        User existing = userRepo.findById(UUID.fromString(currentUserId))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        userRepo.delete(existing);
        return "Profile Deleted Successfully";
    }

    @Override
    public UserResponse getUserProfile(String userId) {
        User user = userRepo.findById(UserHelper.parseUUId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("The Profile Data Is Not Found"));
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse uplaodImgae(MultipartFile file, String userId) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image files allowed");
        }

        if (file.getSize() > 2_000_000) {
            throw new RuntimeException("Image must be less than 2MB");
        }
        User user = userRepo.findById(UserHelper.parseUUId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        try {

            if (user.getImage() != null && user.getImage().contains("cloudinary")) {
                String publicID = extractPublicId(user.getImage());
                cloudinary.uploader().destroy(publicID, Map.of());
            }
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", "profile-images",
                            "resource_type", "image"));
            user.setImage(uploadResult.get("url").toString());

            return mapper.map(userRepo.save(user), UserResponse.class);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("Some Error Occurs To upload Image");
        }
    }

    private String extractPublicId(String image) {
        String[] parts = image.split("/");
        String fileName = parts[parts.length - 1];
        return "profile-images/" + fileName.substring(0, fileName.lastIndexOf("."));
    }

}
