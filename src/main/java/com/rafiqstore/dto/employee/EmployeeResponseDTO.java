package com.rafiqstore.dto.employee;

import com.rafiqstore.entity.Employee;
import com.rafiqstore.entity.SalaryPayment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String designation;
    private double expectedSalary;
    private double totalPaidSalary;
    private double dueSalary;

    public EmployeeResponseDTO(Employee employee) {
        this.id = employee.getId();
        this.name = employee.getName();
        this.address = employee.getAddress();
        this.designation = employee.getDesignation();
        this.expectedSalary = employee.getExpectedSalary();
        this.totalPaidSalary = employee.getSalaryPayments().stream()
                .mapToDouble(SalaryPayment::getPaidAmount)
                .sum();
        this.dueSalary = this.expectedSalary - this.totalPaidSalary;
    }
}