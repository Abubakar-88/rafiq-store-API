package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellItemDetailRequestDTO {
    private Long itemId;
    private String customName;
    private Integer quantity;
    private Double sellPrice;
}
