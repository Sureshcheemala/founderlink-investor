package com.capgemini.team_service.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.capgemini.team_service.config.RabbitMQConstants;
import com.capgemini.team_service.dto.JoinRequestDTO;
import com.capgemini.team_service.dto.NotificationEvent;
import com.capgemini.team_service.dto.StartupResponse;
import com.capgemini.team_service.dto.TeamInviteRequest;
import com.capgemini.team_service.entity.Team;
import com.capgemini.team_service.exception.BadRequestException;
import com.capgemini.team_service.exception.ConflictException;
import com.capgemini.team_service.exception.ResourceNotFoundException;
import com.capgemini.team_service.exception.UnauthorizedException;
import com.capgemini.team_service.fiegn.StartupClient;
import com.capgemini.team_service.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final StartupClient startupClient;

    //Founder invites cofounder
    public Team invite(String founderEmail, TeamInviteRequest request) {

        Team team = Team.builder()
                .startupId(request.getStartupId())
                .userEmail(request.getUserEmail())//cofounder
                .role(request.getRole())
                .status("INVITED")
                .invitedBy(founderEmail)
                .build();

        Team savedTeam = repository.save(team);
        
        sendEvent("TEAM_INVITE", request.getUserEmail(),
        		"You have been invited for role " + savedTeam.getRole(), savedTeam.getId());

        return savedTeam;
    }

    // Cofounder accepts invite
    public Team acceptInvite(Long id, String email) {

        Team team = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

        if (!team.getUserEmail().equals(email)) {
            throw new UnauthorizedException("Not allowed");
        }

        team.setStatus("ACCEPTED");

        Team savedTeam = repository.save(team);
        
        sendEvent("TEAM_INVITE_ACCEPT", team.getInvitedBy(),
        		"Your invite for the role " + savedTeam.getRole() + "has been accepted by the cofounder",
        		savedTeam.getId());

        return savedTeam;
    }

    // Cofounder rejects invite
    public Team rejectInvite(Long id, String email) {

        Team team = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

        if (!team.getUserEmail().equals(email)) {
            throw new UnauthorizedException("Not allowed");
        }

        team.setStatus("REJECTED");

        Team savedTeam = repository.save(team);
        
        sendEvent("TEAM_INVITE_REJECT", team.getInvitedBy(),
        		"Your invite for the role " + savedTeam.getRole() + "has been rejected by the cofounder",
        		savedTeam.getId());

        return savedTeam;
        
    }

    // Cofounder sends join request
    public Team requestToJoin(String email, JoinRequestDTO dto) {

        if (dto.getRole() == null || dto.getRole().isBlank()) {
            throw new BadRequestException("Role is required");
        }

        boolean exists = repository.existsByStartupIdAndUserEmailAndStatus(
                dto.getStartupId(), email, "REQUESTED");

        if (exists) {
            throw new ConflictException("Already requested");
        }

        StartupResponse startup = startupClient.getStartupById(dto.getStartupId());
        
        if (startup == null) {
            throw new ResourceNotFoundException("Startup not found");
        }

        String founderEmail = startup.getFounderEmail();
        
        Team team = Team.builder()
                .startupId(dto.getStartupId())
                .userEmail(dto.getUserEmail())
                .role(dto.getRole())
                .status("REQUESTED")
                .invitedBy(founderEmail)
                .build();

        Team savedTeam = repository.save(team);
        
        sendEvent("TEAM_JOIN_REQUEST", team.getUserEmail(),
        		"New join request for the role " + savedTeam.getRole(),
        		savedTeam.getId());

        return savedTeam;
    }

    // Founder views join requests
    public List<Team> getPendingRequests(Long startupId) {
        return repository.findByStartupIdAndStatus(startupId, "REQUESTED");
    }

    //Founder accepts join request
    public Team acceptRequest(Long id, String founderEmail) {

        Team team = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        team.setStatus("ACCEPTED");

        Team savedTeam = repository.save(team);
        
        sendEvent("TEAM_REQUEST_ACCEPTED", team.getUserEmail(),
        		"Your invite for the role " + savedTeam.getRole() + "has been accepted by the founder",
        		savedTeam.getId());

        return savedTeam;
    }

    // Founder rejects join request
    public Team rejectRequest(Long id, String founderEmail) {

        Team team = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        team.setStatus("REJECTED");
        
        Team savedTeam = repository.save(team);
        sendEvent("TEAM_REQUEST_REJECTED", team.getUserEmail(),
        		"Your invite for the role " + savedTeam.getRole() + "has been rejected by the founder",
        		savedTeam.getId());

        return savedTeam;

    }

    // Get team members
    public List<Team> getStartupTeam(Long startupId) {
        return repository.findByStartupIdAndStatus(startupId, "ACCEPTED");
    }
    
    private void sendEvent(String type, String email, String message, Long id) {
        NotificationEvent event = new NotificationEvent(type, email, message, id);

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.ROUTING_KEY,
                event
        );
    }

}