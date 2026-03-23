package com.capgemini.user_service.service;

import com.capgemini.user_service.dto.UserProfileRequest;
import com.capgemini.user_service.entity.UserProfile;

public interface UserService {

	UserProfile createOrUpdateProfile(String email, UserProfileRequest request);
	
	public UserProfile getProfile(String email);
}
