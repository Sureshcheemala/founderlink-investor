package com.capgemini.startup_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.startup_service.entity.Startup;

public interface StartupRepository extends JpaRepository<Startup, Long>{

	List<Startup> findByStatus(String status);
	
	List<Startup> findByFounderEmail(String founderEmail);
}
