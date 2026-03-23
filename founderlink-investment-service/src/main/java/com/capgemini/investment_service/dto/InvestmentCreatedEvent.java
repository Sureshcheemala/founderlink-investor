package com.capgemini.investment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentCreatedEvent {

    private Long startupId;
    private String investorEmail;
    private Double amount;
}
