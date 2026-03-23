package com.capgemini.user_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.capgemini.user_service.dto.UserProfileRequest;
import com.capgemini.user_service.entity.UserProfile;
import com.capgemini.user_service.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {

	private final UserService userService;

	@PreAuthorize("hasAnyRole('FOUNDER','INVESTOR','COFOUNDER')")
	@PostMapping("/profile")
	public UserProfile createProfile(
	        Authentication authentication,
	        @RequestBody UserProfileRequest request) {

	    String email = authentication.getName();

	    return userService.createOrUpdateProfile(email, request);
	}

	@PreAuthorize("hasAnyRole('FOUNDER','INVESTOR','COFOUNDER')")
	@GetMapping("/profile")
	public UserProfile getProfile(Authentication authentication) {

		return userService.getProfile(authentication.getName());
	}
}