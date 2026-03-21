package com.payguard.apigateway;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // ============================================
                // PUBLIC ROUTES (No JWT required)
                // ============================================
                .route("auth-register", r -> r
                        .path("/api/v1/auth/register")
                        .uri("http://user-service:8081"))

                .route("auth-login", r -> r
                        .path("/api/v1/auth/login")
                        .uri("http://user-service:8081"))

                // ============================================
                // PROTECTED ROUTES (JWT required)
                // ============================================
                .route("user-profile", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://user-service:8081"))

                .route("payments", r -> r
                        .path("/api/v1/payments/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://payment-service:8082"))

                .route("fraud", r -> r
                        .path("/api/v1/fraud/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://fraud-engine:8083"))

                .route("notifications", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://notification-service:8084"))

                .route("reconciliation", r -> r
                        .path("/api/v1/reconciliation/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://reconciliation-service:8085"))

                .build();
    }
}