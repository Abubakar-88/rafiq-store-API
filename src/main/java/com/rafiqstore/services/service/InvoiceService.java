package com.rafiqstore.services.service;

import com.rafiqstore.dto.sellItem.InvoiceResponseDTO;
import com.rafiqstore.entity.Invoice;
import com.rafiqstore.entity.SellItem;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface InvoiceService {
    byte[] generateInvoice(SellItem sellItem) throws IOException;
    String generateInvoiceNumber();
    Page<InvoiceResponseDTO> getAllInvoices(int page, int size, String sort);
    Page<InvoiceResponseDTO> getInvoicesByInvoiceNumber(String invoiceNumber, int page, int size, String sort);
    InvoiceResponseDTO getInvoiceById(Long id);
}
