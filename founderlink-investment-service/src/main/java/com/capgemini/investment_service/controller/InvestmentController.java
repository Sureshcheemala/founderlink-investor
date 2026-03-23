package com.capgemini.investment_service.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.investment_service.dto.InvestmentRequest;
import com.capgemini.investment_service.entity.Investment;
import com.capgemini.investment_service.service.InvestmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    //Only investors can invest
    @PostMapping
    @PreAuthorize("hasRole('INVESTOR')")
    public Investment invest(
            Authentication authentication,
            @RequestBody InvestmentRequest request) {

        String email = authentication.getName();

        return investmentService.createInvestment(email, request);
    }

    // Founder views investments on their startup
    @GetMapping("/startup/{startupId}")
    @PreAuthorize("hasRole('FOUNDER')")
    public List<Investment> getStartupInvestments(@PathVariable Long startupId) {
        return investmentService.getByStartup(startupId);
    }

    // Investor views own investments
    @GetMapping("/me")
    @PreAuthorize("hasRole('INVESTOR')")
    public List<Investment> myInvestments(Authentication auth) {
        return investmentService.getByInvestor(auth.getName());
    }
}
