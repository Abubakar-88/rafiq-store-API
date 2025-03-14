package com.rafiqstore.services.serviceImpl;

import com.rafiqstore.dto.sellItem.InvoiceResponseDTO;
import com.rafiqstore.entity.Invoice;
import com.rafiqstore.entity.SellItem;
import com.rafiqstore.entity.SellItemDetail;
import com.rafiqstore.exception.ResourceNotFoundException;
import com.rafiqstore.repository.InvoiceRepository;
import com.rafiqstore.services.service.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
      private final InvoiceRepository invoiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final ModelMapper modelMapper;
    @Override
    public byte[] generateInvoice(SellItem sellItem) throws IOException {
        logger.info("Generating invoice for SellItem ID: {}", sellItem.getId());
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set font styles
        PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
        PDType1Font normalFont = PDType1Font.HELVETICA;

        // Invoice Header
        contentStream.setFont(titleFont, 18);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Rafiq Print Store");
        contentStream.endText();

        contentStream.setFont(normalFont, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 730);
        contentStream.showText("Invoice Number: " + sellItem.getInvoiceNumber());
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 710);
        contentStream.showText("Buyer: " + sellItem.getBuyerName());
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 690);
        contentStream.showText("Date: " + sellItem.getSellDate());
        contentStream.endText();

        // Add table header
        float yPosition = 650;
        float margin = 50;
        float tableWidth = 500;
        float[] colWidths = {200, 60, 80, 80}; // Column Widths: Item Name, Qty, Price, Subtotal
        float rowHeight = 20;

        // Draw Table Header Background
        contentStream.setNonStrokingColor(220, 220, 220); // Light Gray Background
        contentStream.addRect(margin, yPosition - rowHeight, tableWidth, rowHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(0, 0, 0); // Reset Text Color to Black

        contentStream.setFont(titleFont, 12);
        drawTableText(contentStream, margin, yPosition, colWidths, new String[]{"Item Name", "Qty", "Price", "Subtotal"});

        // Draw table content
        contentStream.setFont(normalFont, 12);
        yPosition -= rowHeight;

        // Add item details
        for (SellItemDetail detail : sellItem.getSellItemDetails()) {
            String itemName;
            if (detail.getItem() != null) {
                // Predefined item
                itemName = detail.getItem().getName();
            } else {
                // Custom item
                itemName = detail.getCustomName();
            }

            // Ensure sellPrice is not null
            if (detail.getSellPrice() == null) {
                logger.error("Sell price is null for item: {}", itemName);
                throw new IllegalArgumentException("Sell price is required for item: " + itemName);
            }

            drawTableText(contentStream, margin, yPosition, colWidths, new String[]{
                    itemName,
                    String.valueOf(detail.getQuantity()),
                    String.format("%.2f", detail.getSellPrice()),
                    String.format("%.2f", detail.getSellPrice() * detail.getQuantity())
            });
            yPosition -= rowHeight;
        }
        // Draw horizontal line
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(margin + tableWidth, yPosition);
        contentStream.stroke();

        // Totals Section
        yPosition -= 30;
        contentStream.setFont(titleFont, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(350, yPosition);
        contentStream.showText("Total Item Amount: " + sellItem.getTotalItemAmount());
        contentStream.endText();

        yPosition -= 20;
        contentStream.beginText();
        contentStream.newLineAtOffset(350, yPosition);
        contentStream.showText("Total Paid Amount: " + sellItem.getTotalPaidAmount());
        contentStream.endText();

        yPosition -= 20;
        contentStream.beginText();
        contentStream.newLineAtOffset(350, yPosition);

        if (sellItem.getTotalDueAmount() == 0) {
            contentStream.setFont(titleFont, 14);
            contentStream.setNonStrokingColor(0, 128, 0); // Green color for "PAID"
            contentStream.showText("PAID");
            contentStream.setNonStrokingColor(0, 0, 0); // Reset to black
        } else {
            contentStream.showText("Total Due Amount: " + sellItem.getTotalDueAmount());
        }

        contentStream.endText();
        contentStream.close();

        // Convert to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    // Function to draw table text with proper alignment
    private void drawTableText(PDPageContentStream contentStream, float x, float y, float[] colWidths, String[] values) throws IOException {
        float cellX = x;
        for (int i = 0; i < values.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(cellX + 5, y - 15);
            contentStream.showText(values[i]);
            contentStream.endText();
            cellX += colWidths[i];
        }
    }


    @Override
    public Page<InvoiceResponseDTO> getAllInvoices(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams.length > 0 ? sortParams[0] : "generatedDate"; // Default to generatedDate
        Sort.Direction sortDirection = sortParams.length > 1 ? Sort.Direction.fromString(sortParams[1]) : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);

        // Map entities to DTOs using ModelMapper
        return invoices.map(invoice -> {
            InvoiceResponseDTO dto = modelMapper.map(invoice, InvoiceResponseDTO.class);
            dto.setBuyerName(invoice.getSellItem().getBuyerName());
            dto.setTotalAmount(invoice.getSellItem().getTotalItemAmount());
            return dto;
        });
    }

    @Override
    public Page<InvoiceResponseDTO> getInvoicesByInvoiceNumber(String invoiceNumber, int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams.length > 0 ? sortParams[0] : "generatedDate"; // Default sort field
        Sort.Direction sortDirection = sortParams.length > 1 ? Sort.Direction.fromString(sortParams[1]) : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        // Fetch invoices from the repository
        Page<Invoice> invoicePage = invoiceRepository.findByInvoiceNumberContaining(invoiceNumber, pageable);

        // Use ModelMapper to map entities to DTOs
        return invoicePage.map(invoice -> modelMapper.map(invoice, InvoiceResponseDTO.class));
    }

    public String generateInvoiceNumber() {
        // Example: INV-20250215-12345
        return "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + new Random().nextInt(10000);
    }
    @Override
    public InvoiceResponseDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getGeneratedDate(),
                invoice.getInvoiceContent()
                // Reference the SellItem ID
        );
    }
}