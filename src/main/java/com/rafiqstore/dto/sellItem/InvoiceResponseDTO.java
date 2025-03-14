package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime generatedDate;
    private String buyerName; // From SellItem
    private Double totalAmount; // From SellItem
     private byte[] invoiceContent;
    public InvoiceResponseDTO(Long id, String invoiceNumber, LocalDateTime generatedDate, byte[] invoiceContent) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.generatedDate = generatedDate;
        this.invoiceContent = invoiceContent;


    }
}
