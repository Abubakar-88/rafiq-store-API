package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesReportDTO {
    private Long itemId;
    private String itemName;
    private Long totalQuantity;
    private Double totalAmount;


}