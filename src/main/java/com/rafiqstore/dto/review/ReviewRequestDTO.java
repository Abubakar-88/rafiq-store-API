package com.rafiqstore.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewRequestDTO {
    private String name;
    private String email;
    private int rating;
    private String comment;
}
