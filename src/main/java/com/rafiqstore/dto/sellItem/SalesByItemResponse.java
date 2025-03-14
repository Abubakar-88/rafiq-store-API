package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesByItemResponse {
    private List<SalesByItemDTO> items;
    private double totalSalesAmount;
    private double totalPaidAmount;
    private double totalDueAmount;
}
