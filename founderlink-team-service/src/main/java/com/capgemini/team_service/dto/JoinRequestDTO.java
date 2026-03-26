package com.capgemini.team_service.dto;

import lombok.Data;

@Data
public class JoinRequestDTO {
    private Long startupId;
    private String userEmail;
    private String role; // CTO, CFO,...
    private String message; // optional
}
