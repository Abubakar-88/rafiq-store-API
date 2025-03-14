package com.rafiqstore.controller;

import com.rafiqstore.dto.sellItem.InvoiceResponseDTO;
import com.rafiqstore.entity.Invoice;
import com.rafiqstore.entity.SellItem;
import com.rafiqstore.exception.ResourceNotFoundException;
import com.rafiqstore.repository.SellItemRepository;
import com.rafiqstore.services.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    @Autowired
    private SellItemRepository sellItemRepository;
    @Autowired
    private InvoiceService invoiceService;
    @GetMapping("/print/{id}")
    public ResponseEntity<ByteArrayResource> printInvoice(@PathVariable Long id) {
        SellItem sellItem = sellItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sell item not found with id: " + id));

        try {
            byte[] pdfContent = invoiceService.generateInvoice(sellItem);
            ByteArrayResource resource = new ByteArrayResource(pdfContent);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate invoice", e);
        }
    }
    @GetMapping
    public ResponseEntity<Page<InvoiceResponseDTO>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "generatedDate,desc") String sort) {
        Page<InvoiceResponseDTO> invoiceResponseDTOs = invoiceService.getAllInvoices(page, size, sort);
        return ResponseEntity.ok(invoiceResponseDTOs);
    }

    // Search invoices by invoice number with pagination
    @GetMapping("/search")
    public ResponseEntity<Page<InvoiceResponseDTO>> searchInvoicesByInvoiceNumber(
            @RequestParam String invoiceNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "generatedDate,desc") String sort) {
        Page<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByInvoiceNumber(invoiceNumber, page, size, sort);
        return ResponseEntity.ok(invoices);
    }
    @GetMapping("/{id}")
    public InvoiceResponseDTO getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);



    }
}
