package com.rafiqstore.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeBasicDTO {
    private Long id;
    private String name;
    private String designation;

}
