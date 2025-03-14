package com.rafiqstore.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class ItemRequestDTO {
    private String name;
    private String description;
    private Integer stock; // Optional field
    private Boolean inStock;

    // ID of the category to which this item belongs
    private Long categoryId;

    // Field to handle existing image name
    private String existingImage;

    public ItemRequestDTO() {
    }
}
