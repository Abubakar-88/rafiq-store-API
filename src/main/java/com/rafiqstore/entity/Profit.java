package com.rafiqstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private double costPrice; // Manually provided cost per unit
    private double totalCost; // Calculated (costPrice * quantitySold)
    private double totalSales; // Calculated (sellPrice * quantitySold)
    private double profit; // Calculated (totalSales - totalCost)

    private int quantitySold; // Total quantity sold
    private LocalDateTime reportDate; // Date of the report



}
