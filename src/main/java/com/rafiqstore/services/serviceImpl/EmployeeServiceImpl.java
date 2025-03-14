package com.rafiqstore.services.serviceImpl;

import com.rafiqstore.dto.employee.EmployeeRequestDTO;
import com.rafiqstore.dto.employee.EmployeeResponseDTO;
import com.rafiqstore.dto.employee.SalaryPaymentRequestDTO;
import com.rafiqstore.dto.employee.SalaryPaymentResponseDTO;
import com.rafiqstore.entity.Employee;
import com.rafiqstore.entity.SalaryPayment;
import com.rafiqstore.exception.ResourceAlreadyExistsException;
import com.rafiqstore.exception.ResourceNotFoundException;
import com.rafiqstore.repository.EmployeeRepository;
import com.rafiqstore.repository.SalaryPaymentRepository;
import com.rafiqstore.services.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryPaymentRepository salaryPaymentRepository;

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = new Employee();
        employee.setName(employeeRequestDTO.getName());
        employee.setAddress(employeeRequestDTO.getAddress());
        employee.setDesignation(employeeRequestDTO.getDesignation());
        employee.setExpectedSalary(employeeRequestDTO.getExpectedSalary());
        return new EmployeeResponseDTO(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(employeeRequestDTO.getName());
                    employee.setAddress(employeeRequestDTO.getAddress());
                    employee.setDesignation(employeeRequestDTO.getDesignation());
                    employee.setExpectedSalary(employeeRequestDTO.getExpectedSalary());
                    return new EmployeeResponseDTO(employeeRepository.save(employee));
                })
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public EmployeeResponseDTO paySalary(Long employeeId, YearMonth paymentMonth, double paidAmount) {
        // Check if the employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Check if salary for the selected month is already paid
        boolean isAlreadyPaid = salaryPaymentRepository.existsByEmployeeIdAndPaymentMonth(employeeId, paymentMonth);
        if (isAlreadyPaid) {
            throw new ResourceAlreadyExistsException("Salary for the selected month has already been paid.");
        }

        // Calculate due salary
        double dueSalary = employee.getExpectedSalary() - paidAmount;

        // Create a new salary payment
        SalaryPayment payment = new SalaryPayment();
        payment.setPaymentMonth(paymentMonth); // Use YearMonth
        payment.setPaidAmount(paidAmount);
        payment.setDueSalary(dueSalary);
        payment.setEmployee(employee);

        // Save the payment
        salaryPaymentRepository.save(payment);

        // Update the employee's salary payments list
        employee.getSalaryPayments().add(payment);

        // Save the employee and return the response
        Employee savedEmployee = employeeRepository.save(employee);
        return new EmployeeResponseDTO(savedEmployee);
    }

    @Override
    public List<SalaryPaymentResponseDTO> getAllSalaryPayments() {
        return salaryPaymentRepository.findAll().stream()
                .map(SalaryPaymentResponseDTO::new)
                .collect(Collectors.toList());
    }
}
