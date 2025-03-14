package com.rafiqstore.services.service;

import com.rafiqstore.dto.employee.EmployeeRequestDTO;
import com.rafiqstore.dto.employee.EmployeeResponseDTO;
import com.rafiqstore.dto.employee.SalaryPaymentRequestDTO;
import com.rafiqstore.dto.employee.SalaryPaymentResponseDTO;
import com.rafiqstore.entity.SalaryPayment;

import java.time.YearMonth;
import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDTO> getAllEmployees();
    EmployeeResponseDTO getEmployeeById(Long id);
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO);
    void deleteEmployee(Long id);
    EmployeeResponseDTO paySalary(Long employeeId, YearMonth paymentMonth, double paidAmount);

   List<SalaryPaymentResponseDTO> getAllSalaryPayments();
}
