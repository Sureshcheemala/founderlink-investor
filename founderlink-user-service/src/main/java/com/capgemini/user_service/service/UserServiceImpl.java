package com.capgemini.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.capgemini.user_service.dto.UserProfileRequest;
import com.capgemini.user_service.entity.UserProfile;
import com.capgemini.user_service.repository.UserProfileRepository;
import com.capgemini.user_service.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserProfileRepository repository;

    @Override
    public UserProfile createOrUpdateProfile(String email, UserProfileRequest request) {

        UserProfile profile = repository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Creating new profile for email: {}", email);
                    return UserProfile.builder().email(email).build();
                });

        profile.setName(request.getName());
        profile.setBio(request.getBio());
        profile.setSkills(request.getSkills());
        profile.setExperience(request.getExperience());
        profile.setPortfolioLinks(request.getPortfolioLinks());

        UserProfile savedProfile = repository.save(profile);

        log.info("Profile saved/updated for email: {}", email);

        return savedProfile;
    }

    @Override
    public UserProfile getProfile(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for email: " + email));
    }
}