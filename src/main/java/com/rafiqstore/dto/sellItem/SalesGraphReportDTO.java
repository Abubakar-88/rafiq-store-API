package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
public class SalesGraphReportDTO {
    private List<SalesData> salesData;

    @Data
    @AllArgsConstructor
    public static class SalesData {
        private LocalDateTime sellDate;
        private Double totalSales;
    }
}
