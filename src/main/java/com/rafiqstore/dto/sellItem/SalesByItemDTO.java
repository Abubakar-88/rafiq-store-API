package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesByItemDTO {
    private Long itemId;
    private String itemName;
    private Integer totalQuantity;
    private Double totalAmount;
}
