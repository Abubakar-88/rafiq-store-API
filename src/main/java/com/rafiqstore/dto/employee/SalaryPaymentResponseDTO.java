package com.rafiqstore.dto.employee;

import com.rafiqstore.entity.Employee;
import com.rafiqstore.entity.SalaryPayment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@NoArgsConstructor
public class SalaryPaymentResponseDTO {
    private Long id;
    private String paymentMonth;
    private double paidAmount;
    private double dueSalary;
    private EmployeeBasicDTO employee; // Use EmployeeBasicDTO instead of Employee

    public SalaryPaymentResponseDTO(SalaryPayment salaryPayment) {
        this.id = salaryPayment.getId();
        this.paymentMonth = formatPaymentMonth(salaryPayment.getPaymentMonth()); // Format month and year
        this.paidAmount = salaryPayment.getPaidAmount();
        this.dueSalary = salaryPayment.getDueSalary();
        this.employee = new EmployeeBasicDTO(
                salaryPayment.getEmployee().getId(),
                salaryPayment.getEmployee().getName(),
                salaryPayment.getEmployee().getDesignation()
        );
    }

    // Helper method to format YearMonth as "Month Year"
    private String formatPaymentMonth(YearMonth paymentMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        return paymentMonth.format(formatter);
    }
}
