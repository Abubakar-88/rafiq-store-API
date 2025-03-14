package com.rafiqstore.controller;

import com.rafiqstore.dto.frontUser.MenuResponseDTO;
import com.rafiqstore.entity.Menu;
import com.rafiqstore.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;


    @GetMapping
    public List<MenuResponseDTO> getAllMenus() {
        return menuService.getAllMenus();
    }


    @PostMapping
    public MenuResponseDTO addMenu(@RequestBody Menu menu) {
        return menuService.addMenu(menu);
    }

    @GetMapping("/{id}")
    public MenuResponseDTO getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }
    @PutMapping("/{id}")
    public MenuResponseDTO updateMenu(@PathVariable Long id, @RequestBody Menu updatedMenu) {
        return menuService.updateMenu(id, updatedMenu);
    }


    @DeleteMapping("/{id}")
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}
