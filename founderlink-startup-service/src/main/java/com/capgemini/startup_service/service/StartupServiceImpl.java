package com.capgemini.startup_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.capgemini.startup_service.config.RabbitMQConstants;
import com.capgemini.startup_service.dto.NotificationEvent;
import com.capgemini.startup_service.dto.StartupRequest;
import com.capgemini.startup_service.entity.Follow;
import com.capgemini.startup_service.entity.Startup;
import com.capgemini.startup_service.exception.ConflictException;
import com.capgemini.startup_service.exception.ResourceNotFoundException;
import com.capgemini.startup_service.exception.UnauthorizedException;
import com.capgemini.startup_service.repository.FollowRepository;
import com.capgemini.startup_service.repository.StartupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StartupServiceImpl implements StartupService{

    private final StartupRepository repository;
    
    private final FollowRepository followRepository;
    
    private final RabbitTemplate rabbitTemplate;

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
        
        Startup savedStartup = repository.save(startup);
        
        sendEvent("INVESTMENT_REJECTED", email,
        		 "New startup created with the name " + savedStartup.getName(), savedStartup.getId());

        return savedStartup;
    }
    
    public Startup updateStartup(Long id, String email, StartupRequest request) {

        Startup startup = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Startup not found"));

        // Ensure only owner can update
        if (!startup.getFounderEmail().equals(email)) {
            throw new UnauthorizedException("Not authorized");
        }

        startup.setName(request.getName());
        startup.setDescription(request.getDescription());
        startup.setIndustry(request.getIndustry());
        startup.setFundingGoal(request.getFundingGoal());
        startup.setStage(request.getStage());

        Startup savedStartup = repository.save(startup);
        
        sendEvent("INVESTMENT_REJECTED", email,
        		 "Startup with the name " + savedStartup.getName() + " Updated", savedStartup.getId());

        return savedStartup;
    }

    public List<Startup> getAllStartups() {
    	return repository.findByStatus("APPROVED");
    }

	@Override
	public List<Startup> getStartups(String industry, String stage, 
			Double minFunding, Double maxFunding, String sortBy) {

		Sort sort = Sort.by(sortBy != null ? sortBy : "fundingGoal");

	    List<Startup> startups = repository.findAll(sort);

	    return startups.stream()
	            .filter(s -> industry == null || s.getIndustry().equalsIgnoreCase(industry))
	            .filter(s -> stage == null || s.getStage().equalsIgnoreCase(stage))
	            .filter(s -> minFunding == null || s.getFundingGoal() >= minFunding)
	            .filter(s -> maxFunding == null || s.getFundingGoal() <= maxFunding)
	            .toList();
	}
	
	public Startup getStartupById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Start not found with the ID: " + id));
	}
	
	public List<Startup> getMyStartups(String email) {
	    return repository.findByFounderEmail(email);
	}
	
	//Admin approving startup
	public Startup approve(Long id, String email) {

	    Startup startup = repository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Startup not found"));

	    startup.setStatus("APPROVED");
	    
	    Startup savedStartup = repository.save(startup);
        
        sendEvent("STARTUP_APPROVED", startup.getFounderEmail(),
        		 "Startup with the name " + savedStartup.getName() + " got approved", savedStartup.getId());

        return savedStartup;
	}
	
	//Admin rejecting the startup
	public Startup reject(Long id, String email) {

	    Startup startup = repository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Startup not found"));

	    startup.setStatus("REJECTED");
	    
	    Startup savedStartup = repository.save(startup);
        
        sendEvent("STARTUP_REJECTED", startup.getFounderEmail(),
        		 "Startup with the name " + savedStartup.getName() + " got approved", savedStartup.getId());

        return savedStartup;
	}
	
	public void followStartup(Long userId, String email) {

	    if (followRepository.existsByUserIdAndEmail(userId, email)) {
	        throw new ConflictException("Already following");
	    }

	    Follow follow = new Follow();
	    follow.setUserId(userId);
	    follow.setEmail(email);
	    follow.setCreatedAt(LocalDateTime.now());

	    followRepository.save(follow);
	    
	    sendEvent("FOLLOWING", follow.getEmail(),
                "You are now following the startup with id " + follow.getStartupId(), follow.getId());
	}
	
	public List<Long> getFollowedStartupIds(String email) {
	    return followRepository.findByEmail(email)
	            .stream()
	            .map(Follow::getStartupId)
	            .toList();
	}
	
	public void deleteStartup(Long id, String email) {

	    Startup startup = repository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Startup not found"));

	    if (!startup.getFounderEmail().equals(email)) {
	        throw new UnauthorizedException("Not authorized");
	    }

	    repository.delete(startup);
	    
	    sendEvent("STARTUP_DELETED", startup.getFounderEmail(),
                "Startup with the Name" + startup.getName() + "Deleted", startup.getId());
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
