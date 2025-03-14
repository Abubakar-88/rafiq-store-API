package com.rafiqstore.repository;

import com.rafiqstore.entity.SalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;

public interface SalaryPaymentRepository extends JpaRepository<SalaryPayment, Long> {
    List<SalaryPayment> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndPaymentMonth(Long employeeId, YearMonth paymentMonth);
}
