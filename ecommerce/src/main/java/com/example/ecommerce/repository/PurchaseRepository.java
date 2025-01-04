package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findAll(Pageable pageable);

    List<Purchase> findByClientId(Long idClient);

    @Query("SELECT p FROM Purchase p " +
            "WHERE (cast(:startDate as date) IS NULL OR p.orderDate >= " +
            ":startDate) " +
            "AND (cast(:endDate as date) IS NULL OR p.orderDate <= :endDate)")
    Page<Purchase> findFilteredByOrderDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
