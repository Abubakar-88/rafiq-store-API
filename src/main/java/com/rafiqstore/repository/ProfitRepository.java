package com.rafiqstore.repository;

import com.rafiqstore.entity.Profit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfitRepository extends JpaRepository<Profit, Long> {

    // Fetch profits for a given date range
    List<Profit> findByReportDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
