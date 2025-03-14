package com.rafiqstore.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private int rating;
    private String comment;

    @Column(name = "is_approved", columnDefinition = "boolean default false")
    private boolean isApproved = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
