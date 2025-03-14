package com.rafiqstore.dto.sellItem;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalSalesDetailsDTO {
    private double totalSalesAmount;
    private double totalPaidAmount;
    private double totalDueAmount;
}
