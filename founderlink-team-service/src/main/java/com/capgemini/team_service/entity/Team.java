package com.capgemini.team_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startupId;

    private String userEmail;

    private String role; // CTO, CPO, etc.
    
    private String type; // INVITE, REQUEST

    private String status; // INVITED, REQUESTED, ACCEPTED

    private String invitedBy; // inviter email
}
