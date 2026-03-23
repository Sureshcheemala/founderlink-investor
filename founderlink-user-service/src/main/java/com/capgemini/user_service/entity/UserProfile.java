package com.capgemini.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // comes from JWT

    private String name;
    private String bio;
    private String skills;
    private String experience;
    private String portfolioLinks;
}