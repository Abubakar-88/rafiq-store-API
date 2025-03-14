package com.rafiqstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    @Lob
    @Column(nullable = false)// Marks the field to be stored as large object in the database
    private byte[] invoiceContent;
    private LocalDateTime generatedDate;

    @ManyToOne
    @JoinColumn(name = "sell_item_id", nullable = false)
    private SellItem sellItem;


}
