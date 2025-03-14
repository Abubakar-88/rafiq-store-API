package com.rafiqstore.services.serviceImpl;

import com.rafiqstore.dto.item.ItemRequestDTO;
import com.rafiqstore.dto.profit.ProfitByItemDTO;
import com.rafiqstore.dto.sellItem.*;
import com.rafiqstore.entity.Invoice;
import com.rafiqstore.entity.Item;
import com.rafiqstore.entity.SellItem;
import com.rafiqstore.entity.SellItemDetail;
import com.rafiqstore.exception.ResourceNotFoundException;
import com.rafiqstore.repository.InvoiceRepository;
import com.rafiqstore.repository.ItemRepository;
import com.rafiqstore.repository.SellItemRepository;
import com.rafiqstore.services.service.InvoiceService;
import com.rafiqstore.services.service.SellItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SellItemServiceImpl implements SellItemService {
    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;


    private final SellItemRepository sellItemRepository;
    private final ModelMapper modelMapper;

    private final InvoiceService invoiceService;

    private static final Logger logger = LoggerFactory.getLogger(SellItemServiceImpl.class);
    @Transactional
    @Override
    public SellItemResponseDTO sellItem(SellItemRequestDTO sellItemRequest) {
        logger.info("Starting sellItem method with request: {}", sellItemRequest);

        SellItem sellItem = new SellItem();
        sellItem.setBuyerName(sellItemRequest.getBuyerName());
        sellItem.setTotalPaidAmount(sellItemRequest.getTotalPaidAmount());
        sellItem.setSellDate(LocalDateTime.now());
        sellItem.setInvoiceNumber(invoiceService.generateInvoiceNumber());
        Double totalSellPrice = 0.0;

        for (SellItemDetailRequestDTO itemRequest : sellItemRequest.getItems()) {
            SellItemDetail sellItemDetail = new SellItemDetail();

            if (itemRequest.getItemId() == null) {
                // Handle custom items
                if (itemRequest.getCustomName() == null) {
                    logger.error("Custom name is required for custom items.");
                    throw new IllegalArgumentException("Custom name is required for custom items.");
                }
                if (itemRequest.getSellPrice() == null) {
                    logger.error("Sell price is required for custom items.");
                    throw new IllegalArgumentException("Sell price is required for custom items.");
                }

                // Generate a random 3-digit number
                int randomNumber = new Random().nextInt(900) + 100; // Generates a number between 100 and 999
                String customNameWithRandomNumber = itemRequest.getCustomName() + "-" + randomNumber;

                sellItemDetail.setCustomName(customNameWithRandomNumber); // Append random number to custom name
                sellItemDetail.setSellPrice(itemRequest.getSellPrice()); // Set sellPrice for custom items
                logger.info("Custom item added: {}", customNameWithRandomNumber);
            } else {
                // Handle predefined items
                Item item = itemRepository.findById(itemRequest.getItemId())
                        .orElseThrow(() -> {
                            logger.error("Item not found with id: {}", itemRequest.getItemId());
                            return new ResourceNotFoundException("Item not found with id: " + itemRequest.getItemId());
                        });

                if (item.getStock() < itemRequest.getQuantity()) {
                    logger.error("Not enough stock available for item: {}", item.getName());
                    throw new IllegalArgumentException("Not enough stock available for item: " + item.getName());
                }

                item.setStock(item.getStock() - itemRequest.getQuantity());
                itemRepository.save(item);

                sellItemDetail.setItem(item); // Ensure item is set
                sellItemDetail.setQuantity(itemRequest.getQuantity());
                sellItemDetail.setSellPrice(itemRequest.getSellPrice());

                logger.info("Predefined item added: {}", item.getName());
            }

            // Ensure quantity is not null
            if (itemRequest.getQuantity() == null) {
                logger.error("Quantity is required for item: {}", itemRequest.getItemId());
                throw new IllegalArgumentException("Quantity is required for item: " + itemRequest.getItemId());
            }

            sellItemDetail.setQuantity(itemRequest.getQuantity());
            sellItemDetail.setSellItem(sellItem);
            sellItem.getSellItemDetails().add(sellItemDetail);

            totalSellPrice += itemRequest.getSellPrice() * itemRequest.getQuantity();
        }

        sellItem.setTotalItemAmount(totalSellPrice);
        sellItem.setTotalDueAmount(totalSellPrice - sellItemRequest.getTotalPaidAmount());

        SellItem savedSellItem = sellItemRepository.save(sellItem);
        logger.info("SellItem saved with ID: {}", savedSellItem.getId());

        // Generate the invoice
        byte[] invoiceContent;
        try {
            invoiceContent = invoiceService.generateInvoice(savedSellItem);
            logger.info("Invoice generated successfully for SellItem ID: {}", savedSellItem.getId());
        } catch (IOException e) {
            logger.error("Failed to generate invoice for SellItem ID: {}", savedSellItem.getId(), e);
            throw new RuntimeException("Failed to generate invoice", e);
        }

        // Save invoice in the database
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(savedSellItem.getInvoiceNumber());
        invoice.setGeneratedDate(LocalDateTime.now());
        invoice.setInvoiceContent(invoiceContent); // Save the byte array
        invoice.setSellItem(savedSellItem);
        invoiceRepository.save(invoice);
        logger.info("Invoice saved with number: {}", invoice.getInvoiceNumber());

        // Map to response DTO
        SellItemResponseDTO responseDTO = modelMapper.map(savedSellItem, SellItemResponseDTO.class);
        responseDTO.setItems(
                savedSellItem.getSellItemDetails().stream().map(detail -> {
                    SellItemDetailResponseDTO detailDTO = new SellItemDetailResponseDTO();

                    if (detail.getItem() != null) {
                        // If Item exists, use its fields
                        detailDTO.setItemId(detail.getItem().getId());
                        detailDTO.setName(detail.getItem().getName());
                    } else {
                        // Handle custom item case
                        detailDTO.setItemId(null); // Or a placeholder for custom items
                        detailDTO.setName(detail.getCustomName()); // Custom name with random number
                    }

                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setSellPrice(detail.getSellPrice());
                    return detailDTO;
                }).collect(Collectors.toList())
        );

        logger.info("sellItem method completed successfully for SellItem ID: {}", savedSellItem.getId());
        return responseDTO;
    }
    @Override
    public List<SellItem> getAllSellItems() {
        return sellItemRepository.findAll();
    }
    @Override
    public SellItemResponseDTO updateSellItem(Long id, SellItemRequestDTO sellItemRequest) {
        // Fetch the existing SellItem from the database
        SellItem sellItem = sellItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SellItem not found"));

        // Update the buyer name and total paid amount from the request
        sellItem.setBuyerName(sellItemRequest.getBuyerName());
        sellItem.setTotalPaidAmount(sellItemRequest.getTotalPaidAmount());

        // Clear existing SellItemDetails (to update with new items)
        sellItem.getSellItemDetails().clear();

        Double totalDueAmount = 0.0;
        Double totalSellPrice = 0.0;
        Double totalItemAmount = 0.0; // New variable for total item amount

        // Process each item in the request
        for (SellItemDetailRequestDTO itemRequest : sellItemRequest.getItems()) {
            SellItemDetail sellItemDetail = new SellItemDetail();

            if (itemRequest.getItemId() == null) {
                // Handle custom items
                if (itemRequest.getCustomName() == null) {
                    logger.error("Custom name is required for custom items.");
                    throw new IllegalArgumentException("Custom name is required for custom items.");
                }
                if (itemRequest.getSellPrice() == null) {
                    logger.error("Sell price is required for custom items.");
                    throw new IllegalArgumentException("Sell price is required for custom items.");
                }

                // Check if the custom item already has a random number
                if (itemRequest.getCustomName().contains("-")) {
                    // Reuse the existing custom name with the random number
                    sellItemDetail.setCustomName(itemRequest.getCustomName());
                } else {
                    // Generate a random 3-digit number and append it to the custom name
                    int randomNumber = new Random().nextInt(900) + 100; // Generates a number between 100 and 999
                    String customNameWithRandomNumber = itemRequest.getCustomName() + "-" + randomNumber;
                    sellItemDetail.setCustomName(customNameWithRandomNumber);
                }

                sellItemDetail.setSellPrice(itemRequest.getSellPrice()); // Set sellPrice for custom items
                logger.info("Custom item added: {}", sellItemDetail.getCustomName());
            } else {
                // Handle predefined items
                Item item = itemRepository.findById(itemRequest.getItemId())
                        .orElseThrow(() -> {
                            logger.error("Item not found with id: {}", itemRequest.getItemId());
                            return new ResourceNotFoundException("Item not found with id: " + itemRequest.getItemId());
                        });

                if (item.getStock() < itemRequest.getQuantity()) {
                    logger.error("Not enough stock available for item: {}", item.getName());
                    throw new IllegalArgumentException("Not enough stock available for item: " + item.getName());
                }

                item.setStock(item.getStock() - itemRequest.getQuantity());
                itemRepository.save(item);

                sellItemDetail.setItem(item); // Ensure item is set
                sellItemDetail.setQuantity(itemRequest.getQuantity());
                sellItemDetail.setSellPrice(itemRequest.getSellPrice());

                logger.info("Predefined item added: {}", item.getName());
            }

            // Ensure quantity is not null
            if (itemRequest.getQuantity() == null) {
                logger.error("Quantity is required for item: {}", itemRequest.getItemId());
                throw new IllegalArgumentException("Quantity is required for item: " + itemRequest.getItemId());
            }

            sellItemDetail.setQuantity(itemRequest.getQuantity());
            sellItemDetail.setSellItem(sellItem);
            sellItem.getSellItemDetails().add(sellItemDetail);

            double itemAmount = itemRequest.getSellPrice() * itemRequest.getQuantity();
            totalSellPrice += itemAmount;
            totalItemAmount += itemAmount; // Add to total item amount
        }

        // Calculate total due amount
        totalDueAmount = totalSellPrice - sellItemRequest.getTotalPaidAmount();
        sellItem.setTotalDueAmount(totalDueAmount);
        sellItem.setTotalItemAmount(totalItemAmount); // Set total item amount

        // Save the updated SellItem record
        SellItem updatedSellItem = sellItemRepository.save(sellItem);

        // Regenerate the invoice as a byte array
        byte[] invoiceContent;
        try {
            invoiceContent = invoiceService.generateInvoice(updatedSellItem);
        } catch (IOException e) {
            throw new RuntimeException("Failed to regenerate invoice", e);
        }

        // Save the invoice data (if required, optionally persist the byte array to a file or database)
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(sellItem.getInvoiceNumber());
        invoice.setGeneratedDate(LocalDateTime.now());
        invoice.setSellItem(sellItem);
        invoice.setInvoiceContent(invoiceContent); // Add a field in the Invoice entity to store the byte array

        sellItem.getInvoices().add(invoice); // Add the invoice to the list

        // Save the SellItem record with the updated invoice
        SellItem savedSellItem = sellItemRepository.save(sellItem);

        // Map SellItem to SellItemResponseDTO
        SellItemResponseDTO responseDTO = modelMapper.map(savedSellItem, SellItemResponseDTO.class);

        // Map SellItemDetails to SellItemDetailResponseDTO
        List<SellItemDetailResponseDTO> itemDetails = savedSellItem.getSellItemDetails().stream()
                .map(detail -> {
                    SellItemDetailResponseDTO detailDTO = new SellItemDetailResponseDTO();

                    if (detail.getItem() != null) {
                        // If Item exists, use its fields
                        detailDTO.setItemId(detail.getItem().getId());
                        detailDTO.setName(detail.getItem().getName());
                    } else {
                        // Handle custom item case
                        detailDTO.setItemId(null); // Or a placeholder for custom items
                        detailDTO.setName(detail.getCustomName()); // Custom name with random number
                    }

                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setSellPrice(detail.getSellPrice());
                    return detailDTO;
                })
                .collect(Collectors.toList());

        responseDTO.setItems(itemDetails);

        // Optionally encode the invoice PDF content as Base64 and include it in the response
        String encodedInvoice = Base64.getEncoder().encodeToString(invoiceContent);
        responseDTO.setEncodedInvoice(encodedInvoice); // Add this field to the DTO if needed

        responseDTO.setTotalItemAmount(totalItemAmount); // Include total item amount in the response

        return responseDTO;
    }

    @Override
    public Page<SellItemResponseDTO> getSellItemsByBuyerNameAndInvoiceNumber(String buyerName, String invoiceNumber, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort.split(",")[1]), sort.split(",")[0]));
        Page<SellItem> sellItems = sellItemRepository.findByBuyerNameContainingIgnoreCaseAndInvoiceNumberContainingIgnoreCase(buyerName, invoiceNumber, pageable);
        return sellItems.map(sellItem -> modelMapper.map(sellItem, SellItemResponseDTO.class));
    }

    @Override
    public Page<SellItemResponseDTO> getAllSellItems(int page, int size, String sort) {
        // Create a Pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort.split(",")[1]), sort.split(",")[0]));

        // Fetch the paginated list of SellItems from the repository
        Page<SellItem> sellItems = sellItemRepository.findAll(pageable);

        // Map SellItem entities to SellItemResponseDTO
        return sellItems.map(sellItem -> {
            SellItemResponseDTO dto = modelMapper.map(sellItem, SellItemResponseDTO.class);
            List<SellItemDetailResponseDTO> itemDetails = sellItem.getSellItemDetails().stream()
                    .map(detail -> {
                        SellItemDetailResponseDTO detailDTO = new SellItemDetailResponseDTO();

                        // Set itemId (if item exists) or null (for custom items)
                        if (detail.getItem() != null) {
                            detailDTO.setItemId(detail.getItem().getId()); // Set itemId for predefined items
                        } else {
                            detailDTO.setItemId(null); // Set itemId as null for custom items
                        }

                        // Set name (use item name for predefined items, custom name for custom items)
                        if (detail.getItem() != null) {
                            detailDTO.setName(detail.getItem().getName()); // Predefined item
                        } else {
                            detailDTO.setName(detail.getCustomName()); // Custom item
                        }

                        // Set quantity and sellPrice
                        detailDTO.setQuantity(detail.getQuantity());
                        detailDTO.setSellPrice(detail.getSellPrice());

                        return detailDTO;
                    })
                    .collect(Collectors.toList());
            dto.setItems(itemDetails);
            return dto;
        });
    }

    public void deleteSellItem(Long id) {
        SellItem sellItem = sellItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SellItem not found"));

        // Delete the invoice file (optional)
        try {
            Files.deleteIfExists(Paths.get("invoice_" + sellItem.getInvoiceNumber() + ".pdf"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete invoice file", e);
        }

        sellItemRepository.delete(sellItem);
    }
    @Override
    public SellItemResponseDTO updatePartialSellItem(Long id, Map<String, Object> updates) {
        SellItem sellItem = sellItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SellItem not found with id " + id));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(SellItem.class, key);
            if (field != null) {
                field.setAccessible(true);
                try {
                    Object convertedValue = convertValueToFieldType(field.getType(), value);
                    ReflectionUtils.setField(field, sellItem, convertedValue);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failed to set value for field: " + key, e);
                }
            }
        });

        // Recalculate dueAmount if totalPaidAmount changes
        if (updates.containsKey("totalPaidAmount")) {
            Double totalPaidAmount = sellItem.getTotalPaidAmount();
            Double totalAmount = sellItem.getTotalItemAmount();

            // If totalPaidAmount >= totalAmount, set dueAmount to 0
            if (totalPaidAmount != null && totalAmount != null) {
                sellItem.setTotalDueAmount(Math.max(0, totalAmount - totalPaidAmount));
            }
        }

        SellItem updatedSellItem = sellItemRepository.save(sellItem);
        return modelMapper.map(updatedSellItem, SellItemResponseDTO.class);
    }

    // Helper method to handle type conversion
    private Object convertValueToFieldType(Class<?> fieldType, Object value) {
        if (fieldType == LocalDateTime.class && value instanceof String) {
            return LocalDateTime.parse((String) value); // Parse the String into LocalDateTime
        }
        if (fieldType == Double.class && value instanceof String) {
            return Double.valueOf((String) value);
        }
        if (fieldType == Integer.class && value instanceof String) {
            return Integer.valueOf((String) value);
        }
        // Add more type conversions as needed
        return value; // Return the original value if no conversion is needed
    }

    @Override
    public Page<SellItemResponseDTO> filterSellItems(
            Double minDueAmount,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sort) {

        // Create a Pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort.split(",")[1]), sort.split(",")[0]));

        // Fetch the filtered and paginated list of SellItems from the repository
        Page<SellItem> sellItems = sellItemRepository.findByFilters(minDueAmount, startDate, endDate, pageable);

        // Map SellItem entities to SellItemResponseDTO
        return sellItems.map(sellItem -> {
            SellItemResponseDTO dto = modelMapper.map(sellItem, SellItemResponseDTO.class);

            // Map SellItemDetails to SellItemDetailResponseDTO
            List<SellItemDetailResponseDTO> itemDetails = sellItem.getSellItemDetails().stream()
                    .map(detail -> {
                        SellItemDetailResponseDTO detailDTO = new SellItemDetailResponseDTO();

                        // Set itemId (if item exists) or null (for custom items)
                        if (detail.getItem() != null) {
                            detailDTO.setItemId(detail.getItem().getId()); // Set itemId for predefined items
                        } else {
                            detailDTO.setItemId(null); // Set itemId as null for custom items
                        }

                        // Set name (use item name for predefined items, custom name for custom items)
                        if (detail.getItem() != null) {
                            detailDTO.setName(detail.getItem().getName()); // Predefined item
                        } else {
                            detailDTO.setName(detail.getCustomName()); // Custom item
                        }

                        // Set quantity and sellPrice
                        detailDTO.setQuantity(detail.getQuantity());
                        detailDTO.setSellPrice(detail.getSellPrice());

                        return detailDTO;
                    })
                    .collect(Collectors.toList());

            dto.setItems(itemDetails);
            return dto;
        });
    }


    @Override
    public SellItemResponseDTO getSellItemById(Long id) {
        // Fetch the sell item from the database
        SellItem sellItem = sellItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SellItem not found with id: " + id));

        // Map SellItem to SellItemResponseDTO
        SellItemResponseDTO responseDTO = modelMapper.map(sellItem, SellItemResponseDTO.class);

        // Map SellItemDetails to SellItemDetailResponseDTO
        List<SellItemDetailResponseDTO> itemDetails = sellItem.getSellItemDetails().stream()
                .map(detail -> {
                    SellItemDetailResponseDTO detailDTO = new SellItemDetailResponseDTO();

                    if (detail.getItem() != null) {
                        // If Item exists, use its fields
                        detailDTO.setItemId(detail.getItem().getId());
                        detailDTO.setName(detail.getItem().getName());
                    } else {
                        // Handle custom item case
                        detailDTO.setItemId(null); // Set itemId as null for custom items
                        detailDTO.setName(detail.getCustomName()); // Use custom name for custom items
                    }

                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setSellPrice(detail.getSellPrice());
                    return detailDTO;
                })
                .collect(Collectors.toList());

        responseDTO.setItems(itemDetails);

        return responseDTO;
    }

    // Sale item reports
    @Override
    public TotalSalesDetailsDTO getTotalSalesDetails(LocalDateTime startDate, LocalDateTime endDate) {
        // Fetch total sales amount
        double totalSalesAmount = sellItemRepository.getTotalSalesAmount(startDate, endDate);

        // Fetch total paid amount
        double totalPaidAmount = sellItemRepository.getTotalPaidAmount(startDate, endDate);

        // Calculate total due amount
        double totalDueAmount = totalSalesAmount - totalPaidAmount;

        return new TotalSalesDetailsDTO(totalSalesAmount, totalPaidAmount, totalDueAmount);
    }
    @Override
    public SalesByItemResponse getSalesByItem(LocalDateTime startDate, LocalDateTime endDate, Long itemId) {
        // Fetch sales by item (both predefined and custom items)
        List<Object[]> results = sellItemRepository.getSalesByItem(startDate, endDate, itemId);

        // Transform the results into a list of SalesByItemDTO
        List<SalesByItemDTO> items = results.stream()
                .map(row -> {
                    Long rowItemId = (Long) row[0]; // itemId (can be null for custom items)
                    String itemName = (String) row[1]; // itemName or customName
                    int totalQuantity = ((Number) row[2]).intValue(); // totalQuantity
                    double totalAmount = ((Number) row[3]).doubleValue(); // totalAmount

                    // If itemId is null, it's a custom item
                    if (rowItemId == null) {
                        return new SalesByItemDTO(null, itemName, totalQuantity, totalAmount);
                    } else {
                        return new SalesByItemDTO(rowItemId, itemName, totalQuantity, totalAmount);
                    }
                })
                .toList();

        // Fetch total paid amount
        double totalPaidAmount = sellItemRepository.getTotalPaidAmount(startDate, endDate);

        // Calculate total sales amount
        double totalSalesAmount = items.stream().mapToDouble(SalesByItemDTO::getTotalAmount).sum();

        // Calculate total due amount
        double totalDueAmount = totalSalesAmount - totalPaidAmount;

        return new SalesByItemResponse(items, totalSalesAmount, totalPaidAmount, totalDueAmount);
    }

    @Override
    public SalesGraphReportDTO getSalesData() {
        // Fetch sales data grouped by sell date
        List<Object[]> results = sellItemRepository.findSalesReport();

        // Transform the results into a List<SalesGraphReportDTO.SalesData>
        List<SalesGraphReportDTO.SalesData> salesDataList = results.stream()
                .map(result -> new SalesGraphReportDTO.SalesData(
                        (LocalDateTime) result[0], // sellDate
                        (Double) result[1]        // totalSales
                ))
                .collect(Collectors.toList());

        // Return the SalesGraphReportDTO containing the list of sales data
        return new SalesGraphReportDTO(salesDataList);
    }

}
