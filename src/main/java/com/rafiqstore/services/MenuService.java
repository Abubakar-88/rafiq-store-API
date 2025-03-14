package com.rafiqstore.services;

import com.rafiqstore.dto.frontUser.MenuResponseDTO;
import com.rafiqstore.entity.Menu;
import com.rafiqstore.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    // সব মেনু পাওয়ার জন্য
    public List<MenuResponseDTO> getAllMenus() {
        List<Menu> menus = menuRepository.findAll();
        return menus.stream()
                .map(menu -> new MenuResponseDTO(menu.getId(), menu.getName(), menu.getDescription()))
                .collect(Collectors.toList());
    }
    public MenuResponseDTO getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));
        return new MenuResponseDTO(menu.getId(), menu.getName(), menu.getDescription());
    }
    // একটি মেনু যোগ করার জন্য
    public MenuResponseDTO addMenu(Menu menu) {
        Menu savedMenu = menuRepository.save(menu);
        return new MenuResponseDTO(savedMenu.getId(), savedMenu.getName(), savedMenu.getDescription());
    }

    // একটি মেনু আপডেট করার জন্য
    public MenuResponseDTO updateMenu(Long id, Menu updatedMenu) {
        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));

        existingMenu.setName(updatedMenu.getName());
        existingMenu.setDescription(updatedMenu.getDescription());

        Menu savedMenu = menuRepository.save(existingMenu);
        return new MenuResponseDTO(savedMenu.getId(), savedMenu.getName(), savedMenu.getDescription());
    }

    // একটি মেনু ডিলিট করার জন্য
    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new RuntimeException("Menu not found with id: " + id);
        }
        menuRepository.deleteById(id);
    }
}