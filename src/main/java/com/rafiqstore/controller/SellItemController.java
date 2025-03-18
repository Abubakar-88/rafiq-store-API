package com.rafiqstore.controller;


import com.rafiqstore.dto.sellItem.SellItemRequestDTO;
import com.rafiqstore.dto.sellItem.SellItemResponseDTO;
import com.rafiqstore.entity.SellItem;
import com.rafiqstore.services.service.SellItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sell-items")
@CrossOrigin(origins = "https://rafiq-printing.netlify.app")
@RequiredArgsConstructor
public class SellItemController {
    private final SellItemService sellItemService;

    @PostMapping("/create")
    public ResponseEntity<SellItemResponseDTO> sellItem(@RequestBody SellItemRequestDTO sellItemRequest) {
        SellItemResponseDTO responseDTO = sellItemService.sellItem(sellItemRequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SellItemResponseDTO> updateSellItem(
            @PathVariable Long id,
            @RequestBody SellItemRequestDTO sellItemRequest) {
        SellItemResponseDTO responseDTO = sellItemService.updateSellItem(id, sellItemRequest);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<SellItemResponseDTO>> getAllSellItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sellDate,desc") String sort) {
        Page<SellItemResponseDTO> sellItems = sellItemService.getAllSellItems(page, size, sort);
        return ResponseEntity.ok(sellItems);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSellItem(@PathVariable Long id) {
        sellItemService.deleteSellItem(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<SellItemResponseDTO> updatePartialSellItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        SellItemResponseDTO updatedSellItem = sellItemService.updatePartialSellItem(id, updates);
        return ResponseEntity.ok(updatedSellItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellItemResponseDTO> getSellItemById(@PathVariable Long id) {
        SellItemResponseDTO responseDTO = sellItemService.getSellItemById(id);
        return ResponseEntity.ok(responseDTO);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<SellItemResponseDTO>> getSellItemsByBuyerNameAndInvoiceNumber(
            @RequestParam(required = false, defaultValue = "") String buyerName,
            @RequestParam(required = false, defaultValue = "") String invoiceNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sellDate,desc") String sort) {
        Page<SellItemResponseDTO> sellItems = sellItemService.getSellItemsByBuyerNameAndInvoiceNumber(buyerName, invoiceNumber, page, size, sort);
        return ResponseEntity.ok(sellItems);
    }
    @GetMapping("/all")
    public List<SellItem> getAllSellItems() {
        return sellItemService.getAllSellItems();
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<SellItemResponseDTO>> filterSellItems(
            @RequestParam(defaultValue = "0") double minDueAmount,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sellDate,desc") String sort) {

        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.MIN;
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.MAX;

        Page<SellItemResponseDTO> sellItems = sellItemService.filterSellItems(minDueAmount, effectiveStartDate, effectiveEndDate, page, size, sort);
        return ResponseEntity.ok(sellItems);
    }


}
