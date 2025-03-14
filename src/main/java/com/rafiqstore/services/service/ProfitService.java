package com.rafiqstore.services.service;

import com.rafiqstore.dto.profit.ProfitResponse;
import com.rafiqstore.dto.sellItem.SellItemResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfitService {
    ProfitResponse calculateProfit(LocalDateTime startDate, LocalDateTime endDate, double costPricePerUnit, Long itemId);
   // ProfitResponse getProfitReport(LocalDateTime startDate, LocalDateTime endDate);
    List<SellItemResponseDTO> getSellItemsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
