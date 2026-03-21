package com.payguard.user.controller;

import com.payguard.user.dto.UserResponse;
import com.payguard.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller - Trusts API Gateway
 * Reads user context from X-User-* headers
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader("X-User-Email") String email
    ) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateUser(
            @RequestHeader("X-User-Email") String email,
            @RequestBody UpdateUserRequest request
    ) {
        UserResponse updated = userService.updateUser(
                email, 
                request.merchantName(), 
                request.country()
        );
        return ResponseEntity.ok(updated);
    }
    
    public record UpdateUserRequest(String merchantName, String country) {}
}