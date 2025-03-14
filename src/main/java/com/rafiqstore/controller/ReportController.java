package com.rafiqstore.controller;

import com.rafiqstore.dto.profit.ErrorResponse;
import com.rafiqstore.dto.profit.ProfitResponse;
import com.rafiqstore.dto.sellItem.*;
import com.rafiqstore.services.service.ProfitService;
import com.rafiqstore.services.service.SellItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ProfitService profitService;

    @Autowired
    private SellItemService sellItemService;

    @GetMapping("/graph")
    public ResponseEntity<SalesGraphReportDTO> getSalesReport() {
        SalesGraphReportDTO salesReport = sellItemService.getSalesData();
        return ResponseEntity.ok(salesReport);
    }
//    @GetMapping("/sales-by-item")
//    public List<Object[]> getSalesByItem(
//            @RequestParam(required = false) LocalDateTime startDate,
//            @RequestParam(required = false) LocalDateTime endDate,
//            @RequestParam(required = false) Long itemId) {
//
//        // Handle null dates
//        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.MIN;
//        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.MAX;
//
//        // Fetch sales data
//        return sellItemService.getSalesByItem(effectiveStartDate, effectiveEndDate, itemId);
//    }

    @GetMapping("/total-sales-details")
    public TotalSalesDetailsDTO getTotalSalesDetails(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {

        // Handle null dates
        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.MIN;
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.MAX;

        // Fetch total sales details
        return sellItemService.getTotalSalesDetails(effectiveStartDate, effectiveEndDate);
    }

    @GetMapping("/sales-by-item")
    public SalesByItemResponse getSalesByItem(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) Long itemId) {

        // Handle null dates
        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.MIN;
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.MAX;

        // Fetch sales by item
        return sellItemService.getSalesByItem(effectiveStartDate, effectiveEndDate, itemId);
    }


    // Generate Profit Report with Manual Item Cost
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateProfit(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam double costPricePerUnit,
            @RequestParam Long itemId) {
        if (costPricePerUnit <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Cost price must be greater than 0."));
        }

        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.MIN;
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.MAX;

        if (effectiveStartDate.isAfter(effectiveEndDate)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Start date cannot be after the end date."));
        }

        ProfitResponse response = profitService.calculateProfit(effectiveStartDate, effectiveEndDate, costPricePerUnit, itemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<SellItemResponseDTO>> getSellItemsByDateRange(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {

        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.MIN;
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.MAX;

        if (effectiveStartDate.isAfter(effectiveEndDate)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<SellItemResponseDTO> sellItems = profitService.getSellItemsByDateRange(effectiveStartDate, effectiveEndDate);
        return ResponseEntity.ok(sellItems);
    }

    // Get Profit Report
//    @GetMapping("/report")
//    public ResponseEntity<ProfitResponse> getProfitReport(
//            @RequestParam(required = false) LocalDateTime startDate,
//            @RequestParam(required = false) LocalDateTime endDate) {
//
//        // Handle null dates
//        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.MIN;
//        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.MAX;
//        ProfitResponse response = profitService.getProfitReport(effectiveStartDate, effectiveEndDate);
//        return ResponseEntity.ok(response);
//    }
}
