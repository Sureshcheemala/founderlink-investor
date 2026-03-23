package com.capgemini.investment_service.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.capgemini.investment_service.dto.InvestmentCreatedEvent;
import com.capgemini.investment_service.dto.InvestmentRequest;
import com.capgemini.investment_service.entity.Investment;
import com.capgemini.investment_service.repository.InvestmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository repository;
    
    private final RabbitTemplate rabbitTemplate;

    public Investment createInvestment(String email, InvestmentRequest request) {

        Investment investment = Investment.builder()
                .startupId(request.getStartupId())
                .investorEmail(email)
                .amount(request.getAmount())
                .status("PENDING")
                .createdAt(java.time.LocalDateTime.now().toString())
                .build();

        InvestmentCreatedEvent event = new InvestmentCreatedEvent(
                investment.getStartupId(),
                investment.getInvestorEmail(),
                investment.getAmount()
        );
        
        rabbitTemplate.convertAndSend("investment.exchange", "investment.created", event);
        
        return repository.save(investment);
    }

    public List<Investment> getByStartup(Long startupId) {
        return repository.findByStartupId(startupId);
    }

    public List<Investment> getByInvestor(String email) {
        return repository.findByInvestorEmail(email);
    }
}
