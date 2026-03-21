package com.payguard.user.service;

import com.payguard.user.dto.UserResponse;
import com.payguard.user.exception.UserNotFoundException;
import com.payguard.user.model.User;
import com.payguard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String email, String merchantName, String country) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        if (merchantName != null) {
            user.setMerchantName(merchantName);
        }
        if (country != null) {
            user.setCountry(country);
        }

        user = userRepository.save(user);
        log.info("User updated: {}", user.getId());

        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .merchantName(user.getMerchantName())
                .merchantCategory(user.getMerchantCategory())
                .country(user.getCountry())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}