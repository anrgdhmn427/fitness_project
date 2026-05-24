package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository repository;

    public UserResponse register(@Valid RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {

            User existingUser = repository.findByEmail(request.getEmail());
            UserResponse userResponse = new UserResponse();
            userResponse.setId(existingUser.getId());
            userResponse.setKeyCloakId(userResponse.getKeyCloakId());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setCrtAt(existingUser.getCrtAt());
            userResponse.setUpdtAt(existingUser.getUpdtAt());
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        User savedUSer = repository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setKeyCloakId(savedUSer.getKeyCloakId());
        userResponse.setId(savedUSer.getId());
        userResponse.setLastName(savedUSer.getLastName());
        userResponse.setFirstName(savedUSer.getFirstName());
        userResponse.setPassword(savedUSer.getPassword());
        userResponse.setEmail(savedUSer.getEmail());
        userResponse.setCrtAt(savedUSer.getCrtAt());
        userResponse.setUpdtAt(savedUSer.getUpdtAt());

        return userResponse;


    }

    public UserResponse getUserProfile(String userId) {

        User user = repository.findById(userId).orElseThrow(() -> new RuntimeException("User Not found"));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setLastName(user.getLastName());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCrtAt(user.getCrtAt());
        userResponse.setUpdtAt(user.getUpdtAt());

        return userResponse;

    }

    public Boolean existByUserId(String userId) {
        log.info("Calling user Validation API for user iD {}", userId);
        return repository.existsById(userId);
    }

    public Boolean existByKeyCloakId(String userId) {
        return repository.existsByKeyCloakId(userId);

    }
}
