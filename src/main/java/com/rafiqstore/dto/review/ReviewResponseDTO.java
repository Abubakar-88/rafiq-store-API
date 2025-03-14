package com.rafiqstore.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String name;
    private String email;
    private int rating;
    private String comment;
    private boolean isApproved;
    private LocalDateTime createdAt;
}
