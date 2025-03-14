package com.rafiqstore.dto.frontUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponseDTO {
    private Long id;
    private String name;
    private String description;
}
