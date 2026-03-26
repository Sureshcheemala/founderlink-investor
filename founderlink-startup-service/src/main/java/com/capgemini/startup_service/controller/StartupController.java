package com.capgemini.startup_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    
    @PreAuthorize("hasAnyRole('FOUNDER','INVESTOR','COFOUNDER')")
    @GetMapping()
    public List<Startup> getStartups(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) Double minFunding,
            @RequestParam(required = false) Double maxFunding,
            @RequestParam(defaultValue = "fundingGoal") String sortBy) {

        return startupService.getStartups(industry, stage, minFunding, maxFunding, sortBy);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FOUNDER')")
    public Startup updateStartup(@PathVariable Long id,
                                @RequestBody StartupRequest request,
                                Authentication authentication) {

        String email = authentication.getName();

        return startupService.updateStartup(id, email, request);
    }
    
    // Founder viewing his startups
    @GetMapping("/my")
    @PreAuthorize("hasRole('FOUNDER')")
    public List<Startup> getMyStartups(Authentication authentication) {

        String email = authentication.getName();

        return startupService.getMyStartups(email);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FOUNDER','INVESTOR','COFOUNDER')")
    public Startup getStartupById(@PathVariable Long id) {
        return startupService.getStartupById(id);
    }
    
    // Approve startup
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Startup approveStartup(@PathVariable Long id, Authentication authentication) {
    	String email = authentication.getName();
        return startupService.approve(id, email);
    }
    
    // Reject startup
    @PutMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Startup rejectStartup(@PathVariable Long id, Authentication authentication) {
    	String email = authentication.getName();
        return startupService.reject(id, email);
    }
    
    @PostMapping("/{id}/follow")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<String> followStartup(@PathVariable Long id,
                                               Authentication authentication) {

        startupService.followStartup(id, authentication.getName());
        return ResponseEntity.ok("Followed successfully");
    }
    
    @GetMapping("/followed")
    @PreAuthorize("hasRole('INVESTOR')")
    public List<Long> getFollowedStartups(Authentication authentication) {

        String email = authentication.getName();

        return startupService.getFollowedStartupIds(email);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FOUNDER')")
    public ResponseEntity<String> deleteStartup(@PathVariable Long id,
                                               Authentication authentication) {

        String email = authentication.getName();

        startupService.deleteStartup(id, email);

        return ResponseEntity.ok("Startup deleted successfully");
    }
}
