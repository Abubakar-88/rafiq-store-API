package com.rafiqstore.repository;

import com.rafiqstore.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByIsApproved(boolean isApproved);
}
