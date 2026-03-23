package com.capgemini.investment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.investment_service.entity.Investment;

public interface InvestmentRepository extends JpaRepository<Investment, Long>{

	List<Investment> findByStartupId(Long startupId);
	
	List<Investment> findByInvestorEmail(String email);
}
