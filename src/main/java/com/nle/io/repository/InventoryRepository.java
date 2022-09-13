package com.nle.io.repository;

import com.nle.io.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the Inventory entity.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

     static final String SEARCH_INVETORY_QUERY = "SELECT inv FROM Inventory inv " +
             "WHERE inv.depoOwnerAccount.companyEmail = :companyEmail ";

    @Query(value = "SELECT inv FROM Inventory inv WHERE inv.depoOwnerAccount.companyEmail = :companyEmail AND inv.gateOutId IS NULL")
    Page<Inventory> getAllInventory (@Param("companyEmail") String companyEmail, Pageable pageable);

    @Query(value = "SELECT * FROM inventories " +
            "WHERE inventories.depo_owner_account_id = :depoId " +
            "AND inventories.container_number = :containerNo " +
            "AND inventories.gate_out_id IS NULL " +
            "LIMIT 1", nativeQuery = true)
    Optional<Inventory> findTopByContainerNumber (@Param("depoId") Long depoId,
            @Param("containerNo") String containerNumber);

    @Query(value = SEARCH_INVETORY_QUERY)
    Page<Inventory> searchByCondition(@Param("companyEmail") String companyEmail,
                                      Pageable pageable);
}
