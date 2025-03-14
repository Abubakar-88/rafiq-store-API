package com.rafiqstore.repository;


import com.rafiqstore.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByName(String name); // Check if an item with the same name already exists
    long count();
}
