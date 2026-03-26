package com.capgemini.investment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.investment_service.entity.InitiatorType;
import com.capgemini.investment_service.entity.Investment;
import com.capgemini.investment_service.entity.InvestmentStatus;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

	List<Investment> findByStartupId(Long startupId);

	List<Investment> findByInvestorEmail(String email);

	List<Investment> findByFounderEmail(String email);

	List<Investment> findByInvestorEmailAndStatus(String investorEmail, InvestmentStatus status);

	List<Investment> findByFounderEmailAndStatus(String email, InvestmentStatus status);

	List<Investment> findByInvestorEmailAndStatusAndInitiatedBy(String investorEmail, InvestmentStatus status,
			InitiatorType initiatedBy);
}
