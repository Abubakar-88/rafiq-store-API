package com.rafiqstore.repository;

import com.rafiqstore.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Page<Invoice> findByInvoiceNumberContaining(String invoiceNumber, Pageable pageable);
    long count();
}
