package com.rafiqstore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafiqstore.dto.item.ItemRequestDTO;
import com.rafiqstore.dto.item.ItemResponseDTO;
import com.rafiqstore.services.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "https://rafiq-printing.netlify.app")
@RequiredArgsConstructor
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponseDTO> createItem(@ModelAttribute ItemRequestDTO itemRequestDTO,
                                                      @RequestPart("imageFile") MultipartFile imageFile) {
        try {
            ItemResponseDTO responseDTO = itemService.createItem(itemRequestDTO, imageFile);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get an item by ID
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Long id) {
        ItemResponseDTO responseDTO = itemService.getItemById(id);
        return ResponseEntity.ok(responseDTO);
    }

    // Get all items
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        List<ItemResponseDTO> responseDTOs = itemService.getAllItems();
        return ResponseEntity.ok(responseDTOs);
    }

    // Update an item
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateItem(
            @PathVariable Long id,
            @ModelAttribute ItemRequestDTO itemRequestDTO,
            @RequestPart("imageFile")MultipartFile imageFile) {
        try {
            ItemResponseDTO updatedItem = itemService.updateItem(id, itemRequestDTO, imageFile);
            return new ResponseEntity<>(updatedItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete an item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
