package com.capgemini.startup_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capgemini.startup_service.dto.StartupRequest;
import com.capgemini.startup_service.entity.Startup;
import com.capgemini.startup_service.repository.StartupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StartupServiceImpl implements StartupService{

    private final StartupRepository repository;

    public Startup createStartup(String email, StartupRequest request) {

        Startup startup = Startup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .industry(request.getIndustry())
                .problemStatement(request.getProblemStatement())
                .solution(request.getSolution())
                .fundingGoal(request.getFundingGoal())
                .stage(request.getStage())
                .founderEmail(email)
                .status("PENDING")
                .build();

        return repository.save(startup);
    }

    public List<Startup> getAllStartups() {
        return repository.findAll();
    }
}
