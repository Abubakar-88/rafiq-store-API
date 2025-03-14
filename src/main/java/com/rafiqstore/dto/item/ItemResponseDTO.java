package com.rafiqstore.dto.item;

import com.rafiqstore.dto.CategoryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer stock; // Can be null
    private Boolean inStock;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryResponseDTO category; // ID of the category to which this item belongs
}
