package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellItemDetailResponseDTO {
    private Long itemId;
    private String name;
    private Integer quantity;
    private Double sellPrice;

    public SellItemDetailResponseDTO( Long itemId, String name, Double sellPrice, Integer quantity) {
        this.itemId = itemId;
        this.name = name;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
    }
}
