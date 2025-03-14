package com.rafiqstore.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeRequestDTO {
    private String name;
    private String address;
    private String designation;
    private double expectedSalary;
}
