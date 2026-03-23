package com.founderlink.auth_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.founderlink.auth_service.dto.AuthResponse;
import com.founderlink.auth_service.dto.LoginRequest;
import com.founderlink.auth_service.dto.RegisterRequest;
import com.founderlink.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }
    
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
    	return authService.login(loginRequest);
    }
    
    @GetMapping("/test-founder")
    @PreAuthorize("hasRole('FOUNDER')")
    public String testFounder() {
        return "Access granted to FOUNDER";
    }
    
    @GetMapping("/test-investor")
    @PreAuthorize("hasRole('INVESTOR')")
    public String testInvestor() {
        return "Access granted to INVESTOR";
    }
}