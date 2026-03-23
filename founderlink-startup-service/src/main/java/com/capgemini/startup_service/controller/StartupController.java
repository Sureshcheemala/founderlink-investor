package com.capgemini.startup_service.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.startup_service.dto.StartupRequest;
import com.capgemini.startup_service.entity.Startup;
import com.capgemini.startup_service.service.StartupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/startups")
@RequiredArgsConstructor
public class StartupController {

    private final StartupService startupService;

    // Only founders can create
    @PostMapping
    @PreAuthorize("hasRole('FOUNDER')")
    public Startup createStartup(
            Authentication authentication,
            @RequestBody StartupRequest request) {

        String email = authentication.getName();

        return startupService.createStartup(email, request);
    }

    // Anyone authenticated can view
    @GetMapping
    public List<Startup> getAllStartups() {
        return startupService.getAllStartups();
    }
    
    @PreAuthorize("hasAnyRole('FOUNDER','INVESTOR')")
    @GetMapping("/search")
    public List<Startup> searchStartups(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) Double minFunding,
            @RequestParam(required = false) Double maxFunding,
            @RequestParam(defaultValue = "fundingGoal") String sortBy) {

        return startupService.searchStartups(industry, stage, minFunding, maxFunding);
    }
}
