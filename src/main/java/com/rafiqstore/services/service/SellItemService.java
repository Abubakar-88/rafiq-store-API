package com.rafiqstore.services.service;

import com.rafiqstore.dto.sellItem.*;
import com.rafiqstore.entity.SellItem;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface SellItemService {
    SellItemResponseDTO sellItem(SellItemRequestDTO sellItemRequest);
    SellItemResponseDTO updateSellItem(Long id, SellItemRequestDTO sellItemRequest);
    void deleteSellItem(Long id);
    Page<SellItemResponseDTO> getSellItemsByBuyerNameAndInvoiceNumber(String buyerName, String invoiceNumber, int page, int size, String sort);
   // Page<SellItemResponseDTO> getSellItemsByBuyerName(String buyerName, int page, int size, String sort);
    SellItemResponseDTO getSellItemById(Long id);
    Page<SellItemResponseDTO> getAllSellItems(int page, int size, String sort);
    SellItemResponseDTO updatePartialSellItem(Long id, Map<String, Object> updates);
    Page<SellItemResponseDTO> filterSellItems(Double minDueAmount, LocalDateTime startDate, LocalDateTime endDate, int page, int size, String sort);
    List<SellItem> getAllSellItems();
    SalesByItemResponse getSalesByItem(LocalDateTime startDate, LocalDateTime endDate, Long itemId);
  //  List<Object[]> getTotalSales(LocalDateTime startDate, LocalDateTime endDate);
    TotalSalesDetailsDTO getTotalSalesDetails(LocalDateTime startDate, LocalDateTime endDate);
   // ProfitReportResponse getProfitReport(LocalDateTime startDate, LocalDateTime endDate);
   SalesGraphReportDTO getSalesData();
}
