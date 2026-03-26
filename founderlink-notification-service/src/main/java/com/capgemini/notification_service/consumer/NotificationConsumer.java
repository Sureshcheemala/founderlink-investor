package com.capgemini.notification_service.consumer;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.capgemini.notification_service.dto.NotificationEvent;
import com.capgemini.notification_service.entity.Notification;
import com.capgemini.notification_service.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;

    @RabbitListener(queues = "notification.queue")
    public void consume(NotificationEvent event) {

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .userEmail(event.getEmail())
                .message(event.getMessage())
                .type(event.getType())
                .isRead(false)
                .status("SENT")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }
}