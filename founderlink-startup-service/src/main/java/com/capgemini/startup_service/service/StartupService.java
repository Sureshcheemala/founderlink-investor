package com.capgemini.startup_service.service;

import java.util.List;

import com.capgemini.startup_service.dto.StartupRequest;
import com.capgemini.startup_service.entity.Startup;

public interface StartupService {

	Startup createStartup(String email, StartupRequest request);
	
	List<Startup> getAllStartups();
	
	List<Startup> searchStartups(String industry, String stage,
            Double minFunding, Double maxFunding);
}
