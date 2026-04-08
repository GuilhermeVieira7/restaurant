package com.restaurant.service;

import com.restaurant.dto.request.UserRequest;
import com.restaurant.dto.response.UserResponse;
import com.restaurant.entity.User;
import com.restaurant.enums.UserRole;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.valueOf(request.getRole().toUpperCase()))
                .active(true)
                .build();
        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse findById(Long id) {
        return toResponse(getUserById(id));
    }

    public UserResponse update(Long id, UserRequest request) {
        User user = getUserById(id);
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use: " + request.getEmail());
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .build();
    }
}
