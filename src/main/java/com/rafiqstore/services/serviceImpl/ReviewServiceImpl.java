package com.rafiqstore.services.serviceImpl;


import com.rafiqstore.dto.review.ReviewRequestDTO;
import com.rafiqstore.dto.review.ReviewResponseDTO;
import com.rafiqstore.entity.Review;
import com.rafiqstore.repository.ReviewRepository;
import com.rafiqstore.services.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ReviewResponseDTO submitReview(ReviewRequestDTO reviewRequestDTO) {
        Review review = modelMapper.map(reviewRequestDTO, Review.class);
        review.setApproved(false); // New reviews are not approved by default
        Review savedReview = reviewRepository.save(review);
        return modelMapper.map(savedReview, ReviewResponseDTO.class);
    }

    @Override
    public List<ReviewResponseDTO> getApprovedReviews() {
        List<Review> approvedReviews = reviewRepository.findByIsApproved(true);
        return approvedReviews.stream()
                .map(review -> modelMapper.map(review, ReviewResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getPendingReviews() {
        List<Review> pendingReviews = reviewRepository.findByIsApproved(false);
        return pendingReviews.stream()
                .map(review -> modelMapper.map(review, ReviewResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setApproved(true);
        reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
