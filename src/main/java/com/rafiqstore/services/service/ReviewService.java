package com.rafiqstore.services.service;

import com.rafiqstore.dto.review.ReviewRequestDTO;
import com.rafiqstore.dto.review.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO submitReview(ReviewRequestDTO reviewRequestDTO);
    List<ReviewResponseDTO> getApprovedReviews();
    List<ReviewResponseDTO> getPendingReviews();
    void approveReview(Long id);
    void deleteReview(Long id);
}
