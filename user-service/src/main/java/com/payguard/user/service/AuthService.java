package com.payguard.user.service;

import com.payguard.user.dto.*;
import com.payguard.user.exception.EmailAlreadyExistsException;
import com.payguard.user.model.Role;
import com.payguard.user.model.User;
import com.payguard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .merchantName(request.getMerchantName())
                .merchantCategory(request.getMerchantCategory())
                .country(request.getCountry())
                .role(Role.MERCHANT)
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getId());
        
        // Publish user.registered event to Kafka
        publishUserRegisteredEvent(user);
        
        return mapToUserResponse(user);
    }
    
    public LoginResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getEmail());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId().toString());
        extraClaims.put("role", user.getRole().name());
        
        String jwtToken = jwtService.generateToken(extraClaims, user);
        
        log.info("User logged in successfully: {}", user.getId());
        
        return LoginResponse.builder()
                .token(jwtToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(mapToUserResponse(user))
                .build();
    }
    
    private void publishUserRegisteredEvent(User user) {
        Map<String, Object> event = Map.of(
                "userId", user.getId().toString(),
                "email", user.getEmail(),
                "merchantName", user.getMerchantName(),
                "eventType", "USER_REGISTERED"
        );
        kafkaTemplate.send("user.registered", user.getId().toString(), event);
        log.debug("Published user.registered event for user: {}", user.getId());
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