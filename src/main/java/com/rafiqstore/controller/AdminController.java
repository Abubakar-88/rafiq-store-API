package com.rafiqstore.controller;

import com.rafiqstore.repository.CategoryRepository;
import com.rafiqstore.repository.InvoiceRepository;
import com.rafiqstore.repository.ItemRepository;
import com.rafiqstore.repository.SellItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SellItemRepository sellItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping("/total-categories")
    public ResponseEntity<Long> getTotalCategories() {
        long totalCategories = categoryRepository.count();
        return ResponseEntity.ok(totalCategories);
    }

    @GetMapping("/total-items")
    public ResponseEntity<Long> getTotalItems() {
        long totalItems = itemRepository.count();
        return ResponseEntity.ok(totalItems);
    }

    @GetMapping("/total-sell-items")
    public ResponseEntity<Long> getTotalSellItems() {
        long totalSellItems = sellItemRepository.count();
        return ResponseEntity.ok(totalSellItems);
    }

    @GetMapping("/total-invoices")
    public ResponseEntity<Long> getTotalInvoices() {
        long totalInvoices = invoiceRepository.count();
        return ResponseEntity.ok(totalInvoices);
    }
}
