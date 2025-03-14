package com.rafiqstore.controller;

import com.rafiqstore.dto.review.ReviewRequestDTO;
import com.rafiqstore.dto.review.ReviewResponseDTO;
import com.rafiqstore.services.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> submitReview(@RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO responseDTO = reviewService.submitReview(reviewRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getApprovedReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getApprovedReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReviewResponseDTO>> getPendingReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveReview(@PathVariable Long id) {
        reviewService.approveReview(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}