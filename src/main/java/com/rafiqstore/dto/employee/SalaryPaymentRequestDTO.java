package com.rafiqstore.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.YearMonth;

@Data
@AllArgsConstructor
public class SalaryPaymentRequestDTO {
    private String paymentMonth; // Ensure this is a String (e.g., "2023-10")
    private double paidAmount;
}
