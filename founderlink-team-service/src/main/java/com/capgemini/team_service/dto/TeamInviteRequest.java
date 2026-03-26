package com.capgemini.team_service.dto;

import lombok.Data;

@Data
public class TeamInviteRequest {
    private Long startupId;
    private String userEmail;
    private String role;
}
