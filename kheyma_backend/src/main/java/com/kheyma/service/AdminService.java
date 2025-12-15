package com.kheyma.service;

import com.kheyma.dto.*;
import com.kheyma.model.User;
import com.kheyma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    public PageResponse<UserDto> getAllUsers(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;
        
        if (filter != null && !filter.isEmpty()) {
            // Simple filter by email or name - can be enhanced
            users = userRepository.findAll(pageable);
            List<User> filtered = users.getContent().stream()
                    .filter(user -> user.getEmail().toLowerCase().contains(filter.toLowerCase()) ||
                                   (user.getName() != null && user.getName().toLowerCase().contains(filter.toLowerCase())))
                    .collect(Collectors.toList());
            users = new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
        } else {
            users = userRepository.findAll(pageable);
        }
        
        List<UserDto> content = users.getContent().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }
    
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromUser(user);
    }
    
    @Transactional
    public UserDto updateUserRole(String id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            User.Role newRole = User.Role.valueOf(role);
            user.getRoles().clear();
            user.getRoles().add(newRole);
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }
        
        return UserDto.fromUser(user);
    }
    
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Soft delete by deactivating
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}

