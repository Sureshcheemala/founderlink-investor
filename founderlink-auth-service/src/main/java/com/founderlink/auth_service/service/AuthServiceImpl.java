package com.founderlink.auth_service.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.founderlink.auth_service.dto.AuthResponse;
import com.founderlink.auth_service.dto.LoginRequest;
import com.founderlink.auth_service.dto.RegisterRequest;
import com.founderlink.auth_service.entity.Role;
import com.founderlink.auth_service.entity.User;
import com.founderlink.auth_service.exception.ResourceNotFoundException;
import com.founderlink.auth_service.repository.RoleRepository;
import com.founderlink.auth_service.repository.UserRepository;
import com.founderlink.auth_service.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;
	private final JwtService jwtService;

	@Override
	public AuthResponse registerUser(RegisterRequest registerRequest) {

		// checking if email already exists
		userRepository.findByEmail(registerRequest.getEmail()).ifPresent(user -> {
			throw new ResourceNotFoundException("Email already exists");
		});
		
		//Getting role details and checking if it exists
		Role role = roleRepository.findByName(registerRequest.getRole())
				.orElseThrow(() -> new ResourceNotFoundException("Role does not exist"));
		
		//Converting role into set
		Set<Role> roles = Set.of(role);
		
		//creating and save user
		User user = User.builder()
				.name(registerRequest.getName())
				.email(registerRequest.getEmail())
				.password(encoder.encode(registerRequest.getPassword()))
				.createdAt(LocalDateTime.now())
				.roles(roles)
				.build();
		userRepository.save(user);
		
		return AuthResponse.builder()
				.token("User registered successfully")
				.build();
	}

	@Override
	public AuthResponse login(LoginRequest loginRequest) {
		
		//checking if the user exists or not
		User user = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not Found with the email"));
		
		//checking password match
		if(!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new ResourceNotFoundException("Invalid credentials");
		}
		
		//generating jwt token
		String token = jwtService.generateToken(user);
		return AuthResponse.builder().token(token).build();
	}
	
	public AuthResponse refreshToken(String refreshToken) {

        // 1. Validate refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 2. Extract email
        String email = jwtService.extractUsername(refreshToken);

        // 3. Fetch user (IMPORTANT)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Generate new tokens
        String accessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        // 5. Return response
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
	
	 public void blockUser(Long userId) {

	        User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	        user.setActive(false);

	        userRepository.save(user);
	    }
}
