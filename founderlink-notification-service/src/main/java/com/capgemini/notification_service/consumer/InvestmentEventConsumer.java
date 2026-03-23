package com.capgemini.notification_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.capgemini.notification_service.dto.InvestmentCreatedEvent;

@Service
public class InvestmentEventConsumer {

    @RabbitListener(queues = "notification.queue")
    public void consume(InvestmentCreatedEvent event) {

        System.out.println("New Investment Event Received");
        System.out.println("Startup ID: " + event.getStartupId());
        System.out.println("Investor: " + event.getInvestorEmail());
        System.out.println("Amount: " + event.getAmount());
    }
}