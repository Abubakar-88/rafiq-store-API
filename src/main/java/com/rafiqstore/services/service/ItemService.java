package com.rafiqstore.services.service;


import com.rafiqstore.dto.item.ItemRequestDTO;
import com.rafiqstore.dto.item.ItemResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ItemService {
    ItemResponseDTO createItem(ItemRequestDTO itemRequestDTO, MultipartFile imageFile) throws IOException;
    ItemResponseDTO getItemById(Long id);
    List<ItemResponseDTO> getAllItems();
    ItemResponseDTO updateItem(Long id, ItemRequestDTO itemRequestDTO,  MultipartFile imageFile) throws IOException;
    void deleteItem(Long id);
}