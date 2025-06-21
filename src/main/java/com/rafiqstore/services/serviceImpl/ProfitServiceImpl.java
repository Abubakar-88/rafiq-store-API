package com.rafiqstore.services.serviceImpl;

import com.rafiqstore.dto.profit.ProfitResponse;
import com.rafiqstore.dto.sellItem.InvoiceResponseDTO;
import com.rafiqstore.dto.sellItem.SellItemDetailRequestDTO;
import com.rafiqstore.dto.sellItem.SellItemDetailResponseDTO;
import com.rafiqstore.dto.sellItem.SellItemResponseDTO;
import com.rafiqstore.entity.SellItemDetail;
import com.rafiqstore.entity.Profit;
import com.rafiqstore.entity.SellItem;
import com.rafiqstore.repository.ItemRepository;
import com.rafiqstore.repository.ProfitRepository;
import com.rafiqstore.repository.SellItemRepository;
import com.rafiqstore.services.service.ProfitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfitServiceImpl implements ProfitService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProfitRepository profitRepository;

    @Autowired
    private SellItemRepository sellItemRepository;

    @Override
    public ProfitResponse calculateProfit(LocalDateTime startDate, LocalDateTime endDate, double costPricePerUnit, Long itemId) {
        // Validate cost price
        if (costPricePerUnit <= 0) {
            throw new IllegalArgumentException("Cost price per unit must be greater than 0.");
        }

        // Fetch SellItems within the date range and for the specific item
        List<SellItem> sellItems = sellItemRepository.findBySellDateBetweenAndSellItemDetails_ItemId(startDate, endDate, itemId);

        if (sellItems == null || sellItems.isEmpty()) {
            return new ProfitResponse(0.0); // No items found, profit is 0
        }

        // Calculate total sales and total cost in a single stream
        double totalSales = 0.0;
        int totalQuantity = 0;

        for (SellItem sellItem : sellItems) {
            for (SellItemDetail detail : sellItem.getSellItemDetails()) {
                if (detail.getItem().getId().equals(itemId)) {
                    totalSales += detail.getSellPrice() * detail.getQuantity();
                    totalQuantity += detail.getQuantity();
                }
            }
        }

        double totalCost = costPricePerUnit * totalQuantity;
        double profit = totalSales - totalCost;

        return new ProfitResponse(profit);
    }


@Override
public List<SellItemResponseDTO> getSellItemsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    List<SellItem> sellItems = sellItemRepository.findSellItemsWithDetailsByDateRange(startDate, endDate);

    if (sellItems == null || sellItems.isEmpty()) {
        return Collections.emptyList(); // Return empty list if no items found
    }

    return sellItems.stream()
            .map(sellItem -> new SellItemResponseDTO(
                    sellItem.getId(),
                    sellItem.getBuyerName(),
                    sellItem.getTotalPaidAmount(),
                    sellItem.getTotalItemAmount(),
                    sellItem.getSellDate(),
                    sellItem.getSellItemDetails().stream()
                            .map(detail -> {
                                // Handle predefined and custom items
                                Long itemId = (detail.getItem() != null) ? detail.getItem().getId() : null; // Set itemId for predefined items, null for custom items
                                String itemName = (detail.getItem() != null) ? detail.getItem().getName() : detail.getCustomName(); // Use item name for predefined items, custom name for custom items

                                return new SellItemDetailResponseDTO(
                                        itemId, // itemId
                                        itemName, // itemName
                                        detail.getSellPrice(), // sellPrice
                                        detail.getQuantity() // quantity
                                );
                            })
                            .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
}



}
