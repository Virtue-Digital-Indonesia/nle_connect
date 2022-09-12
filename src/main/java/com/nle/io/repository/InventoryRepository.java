package com.nle.io.repository;

import com.nle.io.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Inventory entity.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    @Query(value = "SELECT inv FROM Inventory inv WHERE inv.depoOwnerAccount.companyEmail = :companyEmail AND inv.gateOutId IS NULL")
    Page<Inventory> getAllInventory (@Param("companyEmail") String companyEmail, Pageable pageable);
}
