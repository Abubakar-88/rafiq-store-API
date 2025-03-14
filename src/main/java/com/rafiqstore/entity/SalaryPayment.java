package com.rafiqstore.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.YearMonth;

@Entity
@Data
public class SalaryPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private YearMonth paymentMonth;
    private double paidAmount;
    private double dueSalary;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}