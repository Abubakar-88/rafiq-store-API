package com.rafiqstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class SellItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version = 0;

    private String buyerName;
    private Double totalPaidAmount;
    private Double totalDueAmount;
    private Double totalItemAmount;
    private LocalDateTime sellDate;
    private String invoiceNumber;

    @OneToMany(mappedBy = "sellItem", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<SellItemDetail> sellItemDetails = new ArrayList<>();

    @OneToMany(mappedBy = "sellItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();
}
