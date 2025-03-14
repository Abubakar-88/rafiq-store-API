package com.rafiqstore.dto.profit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfitByItemDTO {
    private Long itemId;
    private String itemName;
    private int totalQuantity;
    private double totalSales;
    private double totalCost;
    private double totalProfit;
}
