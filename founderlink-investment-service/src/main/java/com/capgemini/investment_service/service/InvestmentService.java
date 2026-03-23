package com.capgemini.investment_service.service;

import java.util.List;

import com.capgemini.investment_service.dto.InvestmentRequest;
import com.capgemini.investment_service.entity.Investment;

public interface InvestmentService {
	
	Investment createInvestment(String email, InvestmentRequest request);
	
	List<Investment> getByStartup(Long startupId);
	
	List<Investment> getByInvestor(String email);
}
