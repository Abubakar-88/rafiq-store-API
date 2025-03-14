package com.rafiqstore.controller;


import com.rafiqstore.dto.employee.EmployeeRequestDTO;
import com.rafiqstore.dto.employee.EmployeeResponseDTO;
import com.rafiqstore.dto.employee.SalaryPaymentRequestDTO;
import com.rafiqstore.dto.employee.SalaryPaymentResponseDTO;
import com.rafiqstore.entity.SalaryPayment;
import com.rafiqstore.exception.ResourceAlreadyExistsException;
import com.rafiqstore.exception.ResourceNotFoundException;
import com.rafiqstore.repository.SalaryPaymentRepository;
import com.rafiqstore.services.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SalaryPaymentRepository salaryPaymentRepository;

    @GetMapping
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeResponseDTO getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public EmployeeResponseDTO createEmployee(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return employeeService.createEmployee(employeeRequestDTO);
    }

    @PutMapping("/{id}")
    public EmployeeResponseDTO updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return employeeService.updateEmployee(id, employeeRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @PostMapping("/{id}/pay-salary")
    public ResponseEntity<?> paySalary(
            @PathVariable Long id,
            @RequestBody SalaryPaymentRequestDTO salaryPaymentRequestDTO
    ) {
        try {
            // Convert paymentMonth from String to YearMonth
            YearMonth paymentMonth = YearMonth.parse(salaryPaymentRequestDTO.getPaymentMonth());

            // Call the service method with the parsed YearMonth
            EmployeeResponseDTO response = employeeService.paySalary(id, paymentMonth, salaryPaymentRequestDTO.getPaidAmount());
            return ResponseEntity.ok(response);
        } catch (ResourceAlreadyExistsException e) {
            // Return a 400 Bad Request with the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            // Return a 404 Not Found with the error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            // Handle other runtime exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }
    // Get all salary payments
    @GetMapping("/salary-payments")
    public List<SalaryPaymentResponseDTO> getAllSalaryPayments() {
        return employeeService.getAllSalaryPayments();
    }
    @PutMapping("/salary-payments/{id}")
    public SalaryPayment updateSalaryPayment(
            @PathVariable Long id,
            @RequestBody SalaryPaymentRequestDTO salaryPaymentRequestDTO
    ) {
        return salaryPaymentRepository.findById(id)
                .map(payment -> {
                    payment.setPaymentMonth(YearMonth.parse(salaryPaymentRequestDTO.getPaymentMonth()));
                    payment.setPaidAmount(salaryPaymentRequestDTO.getPaidAmount());
                    payment.setDueSalary(payment.getEmployee().getExpectedSalary() - salaryPaymentRequestDTO.getPaidAmount());
                    return salaryPaymentRepository.save(payment);
                })
                .orElseThrow(() -> new RuntimeException("Salary payment not found"));
    }


    @DeleteMapping("/salary-payments/{id}")
    public void deleteSalaryPayment(@PathVariable Long id) {
        salaryPaymentRepository.deleteById(id);
    }


    @GetMapping("/salary-payments/{id}")
    public SalaryPaymentResponseDTO getSalaryPaymentById(@PathVariable Long id) {
        return salaryPaymentRepository.findById(id)
                .map(SalaryPaymentResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Salary payment not found"));
    }












}