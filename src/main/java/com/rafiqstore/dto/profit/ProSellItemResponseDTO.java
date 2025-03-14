package com.rafiqstore.dto.profit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProSellItemResponseDTO {
    private Long id;
    private String name;
    private double sellPrice;
    private int quantity;
    private LocalDateTime sellDate;
    private Long itemId;
}
