package com.rafiqstore.repository;

import com.rafiqstore.dto.sellItem.SalesGraphReportDTO;
import com.rafiqstore.entity.Invoice;
import com.rafiqstore.entity.SellItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SellItemRepository extends JpaRepository<SellItem, Long> {



   Page<SellItem> findByBuyerNameContainingIgnoreCaseAndInvoiceNumberContainingIgnoreCase(String buyerName, String invoiceNumber, Pageable pageable);

    List<SellItem> findBySellDateBetweenAndSellItemDetails_ItemId(LocalDateTime startDate, LocalDateTime endDate, Long itemId);

 //   List<SellItem> findBySellDateBetween(LocalDateTime startDate, LocalDateTime endDate);

//    @Query("SELECT s FROM SellItem s JOIN FETCH s.sellItemDetails sid " +
//            "WHERE s.sellDate BETWEEN :startDate AND :endDate")
//    List<SellItem> findSellItemsWithDetailsByDateRange(@Param("startDate") LocalDateTime startDate,
//                                                       @Param("endDate") LocalDateTime endDate);
@Query("SELECT DISTINCT s FROM SellItem s JOIN FETCH s.sellItemDetails sid " +
        "WHERE s.sellDate BETWEEN :startDate AND :endDate")
List<SellItem> findSellItemsWithDetailsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM SellItem s WHERE " +
            "(:minDueAmount IS NULL OR s.totalDueAmount >= :minDueAmount) AND " +
            "(:startDate IS NULL OR s.sellDate >= :startDate) AND " +
            "(:endDate IS NULL OR s.sellDate <= :endDate)")
    Page<SellItem> findByFilters(
            @Param("minDueAmount") Double minDueAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
           long count();

    @Query("SELECT 0L, 'Total Sales', 0L, SUM(s.totalPaidAmount) " +
            "FROM SellItem s " +
            "WHERE s.sellDate BETWEEN :startDate AND :endDate")
    List<Object[]> getTotalSales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Query("SELECT COALESCE(SUM(s.totalPaidAmount), 0) FROM SellItem s WHERE s.sellDate BETWEEN :startDate AND :endDate")
    double getTotalPaidAmount(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(s.totalItemAmount), 0) FROM SellItem s WHERE s.sellDate BETWEEN :startDate AND :endDate")
    double getTotalSalesAmount(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    @Query(value = "SELECT " +
            "CASE WHEN sd.item_id IS NOT NULL THEN sd.item_id ELSE NULL END AS itemId, " + // itemId (null for custom items)
            "CASE WHEN sd.item_id IS NOT NULL THEN i.name ELSE sd.customName END AS itemName, " + // itemName or customName
            "SUM(sd.quantity) AS totalQuantity, " + // totalQuantity
            "SUM(sd.quantity * sd.sellPrice) AS totalAmount " + // totalAmount
            "FROM sellitem s " +
            "JOIN sellitemdetail sd ON s.id = sd.sell_item_id " +
            "LEFT JOIN items i ON sd.item_id = i.id " + // LEFT JOIN to include items even if item_id is null
            "WHERE s.sellDate BETWEEN :startDate AND :endDate " +
            "AND (:itemId IS NULL OR sd.item_id = :itemId) " + // Filter by itemId (if provided)
            "GROUP BY sd.item_id, i.name, sd.customName", // Group by itemId, itemName, or customName
            nativeQuery = true) // Use native SQL query
    List<Object[]> getSalesByItem(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("itemId") Long itemId
    );

//    @Query("SELECT sd.item.id, sd.item.name, SUM(sd.quantity), SUM(sd.sellPrice * sd.quantity) " +
//            "FROM SellItem s " +
//            "JOIN s.sellItemDetails sd " +
//            "WHERE s.sellDate BETWEEN :startDate AND :endDate " +
//            "AND (:itemId IS NULL OR sd.item.id = :itemId) " +
//            "GROUP BY sd.item.id, sd.item.name")
//    List<Object[]> getSalesByItem(
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate,
//            @Param("itemId") Long itemId
//    );

//    @Query("""
//    SELECT
//        sd.item.id AS itemId,
//        sd.item.name AS itemName,
//        SUM(sd.quantity) AS totalQuantity,
//        SUM(sd.sellPrice * sd.quantity) AS totalSales,
//        SUM(sd.costPrice * sd.quantity) AS totalCost,
//        (SUM(sd.sellPrice * sd.quantity) - SUM(sd.costPrice * sd.quantity)) AS totalProfit
//    FROM SellItem s
//    JOIN s.sellItemDetails sd
//    WHERE s.sellDate BETWEEN :startDate AND :endDate
//    GROUP BY sd.item.id, sd.item.name
//""")
//    List<Object[]> getProfitByItem(
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate);
//
@Query("SELECT s.sellDate, SUM(s.totalPaidAmount) " +
        "FROM SellItem s " +
        "GROUP BY s.sellDate " +
        "ORDER BY s.sellDate ASC")
List<Object[]> findSalesReport();
}
