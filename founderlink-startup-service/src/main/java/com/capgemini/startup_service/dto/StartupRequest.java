package com.capgemini.startup_service.dto;

import lombok.Data;

@Data
public class StartupRequest {
    private String name;
    private String description;
    private String industry;
    private String problemStatement;
    private String solution;
    private Double fundingGoal;
    private String stage;
}