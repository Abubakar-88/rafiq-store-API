package com.rafiqstore.dto.sellItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellItemRequestDTO {
    private String buyerName;
    private Double totalPaidAmount;
    private Double totalItemAmount;
    private List<SellItemDetailRequestDTO> items;
}
