package com.rafiqstore.services.serviceImpl;


import com.rafiqstore.dto.item.ItemRequestDTO;
import com.rafiqstore.dto.item.ItemResponseDTO;
import com.rafiqstore.entity.Category;
import com.rafiqstore.entity.Item;
import com.rafiqstore.exception.ResourceAlreadyExistsException;
import com.rafiqstore.exception.ResourceNotFoundException;
import com.rafiqstore.repository.CategoryRepository;
import com.rafiqstore.repository.ItemRepository;
import com.rafiqstore.services.service.ItemService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Value("${image.upload.dir}")
    private String uploadDir;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    @Override
    public ItemResponseDTO createItem(ItemRequestDTO itemRequestDTO, MultipartFile imageFile) throws IOException {
        // Check if an item with the same name already exists
        if (itemRepository.existsByName(itemRequestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Item with the same name already exists!");
        }
        if (itemRequestDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        // Fetch the category by ID
        Category category = categoryRepository.findById(itemRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + itemRequestDTO.getCategoryId()));

        // Map DTO to Entity
        Item item = modelMapper.map(itemRequestDTO, Item.class);
        item.setCategory(category);
        item.setId(null); // Ensure id is null for new entities
        // Save the image and get the file path or URL
        if (!imageFile.isEmpty()){
            String fileName = saveImage(imageFile, item);
            item.setImage(fileName);
        }
        // Handle null stock
        if (itemRequestDTO.getStock() == null) {
            item.setInStock(false); // If stock is null, set inStock to false
        } else {
            item.setInStock(itemRequestDTO.getStock() > 0); // Set inStock based on stock value
        }


        log.info("Before saving: Item version = {}", item.getVersion());
        Item savedItem = itemRepository.save(item);
        log.info("After saving: Item version = {}", savedItem.getVersion());

        // Map Entity to DTO and return
        return modelMapper.map(savedItem, ItemResponseDTO.class);
    }
    public String saveImage(MultipartFile file, Item item) throws IOException {
        // If no file is provided, return null (indicating no image was uploaded)
        if (file == null || file.isEmpty()) {
            return null; // Image upload is optional
        }

        // Ensure the upload directory exists
        Path pathDir = Paths.get(uploadDir + "/item");
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir); // Create directory if it doesn't exist
        }

        // Extract the file extension
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.lastIndexOf(".") == -1) {
            throw new IllegalArgumentException("Invalid file name or extension");
        }
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // Generate a unique file name
        String fileName = item.getName() + "_" + UUID.randomUUID() + extension;

        // Save the file to the upload directory
        Path filePath = pathDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // Avoid duplicate files

        return fileName;// Return the file name for database storage
    }
    @Override
    public ItemResponseDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return modelMapper.map(item, ItemResponseDTO.class);
    }

    @Override
    public List<ItemResponseDTO> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(item -> modelMapper.map(item, ItemResponseDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public ItemResponseDTO updateItem(Long id, ItemRequestDTO itemRequestDTO, MultipartFile imageFile) throws IOException {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        // Fetch the category
        Category category = categoryRepository.findById(itemRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + itemRequestDTO.getCategoryId()));

        // Update item fields
        item.setName(itemRequestDTO.getName());
        item.setDescription(itemRequestDTO.getDescription());
        item.setStock(itemRequestDTO.getStock());
        item.setInStock(itemRequestDTO.getStock() != null && itemRequestDTO.getStock() > 0);
        item.setCategory(category);

        // Handle image file
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile, item);
            item.setImage(fileName);
        } else if (itemRequestDTO.getExistingImage() != null) {
            item.setImage(itemRequestDTO.getExistingImage());
        }

        // Save and return updated item
        Item updatedItem = itemRepository.save(item);
        return modelMapper.map(updatedItem, ItemResponseDTO.class);
    }


    @Override
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        itemRepository.delete(item);
    }
}
