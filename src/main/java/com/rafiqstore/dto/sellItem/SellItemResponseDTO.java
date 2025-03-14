package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellItemResponseDTO {
    private Long id;
    private String buyerName;
    private Double totalPaidAmount;
    private Double totalDueAmount;
    private Double totalItemAmount;
    private LocalDateTime sellDate;
    private String invoiceNumber;
    private String encodedInvoice;
    private List<SellItemDetailResponseDTO> items;

    // Constructor
    public SellItemResponseDTO(Long id, String buyerName, Double totalPaidAmount,
                               Double totalItemAmount, LocalDateTime sellDate,
                               List<SellItemDetailResponseDTO> items) {
        this.id = id;
        this.buyerName = buyerName;
        this.totalPaidAmount = totalPaidAmount;
        this.totalItemAmount = totalItemAmount;
        this.sellDate = sellDate;
        this.items = items;
    }
}
