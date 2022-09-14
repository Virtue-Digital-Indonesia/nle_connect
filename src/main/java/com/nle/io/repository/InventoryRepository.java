package com.nle.io.repository;

import com.nle.io.entity.Inventory;
import com.nle.ui.model.request.search.InventorySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data SQL repository for the Inventory entity.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

     static final String SEARCH_INVETORY_QUERY = "SELECT inv FROM Inventory inv " +
             "WHERE inv.depoOwnerAccount.companyEmail = :companyEmail " +
             "AND inv.gateOutId IS NULL " +
             "AND (:#{#request.depot} IS NULL OR LOWER(inv.depot) LIKE LOWER(CONCAT('%', :#{#request.depot}, '%'))) " +
             "AND (:#{#request.fleet_manager} IS NULL OR LOWER(inv.fleet_manager) LIKE LOWER(CONCAT('%', :#{#request.fleet_manager}, '%'))) " +
             "AND (:#{#request.container_number} IS NULL OR LOWER(inv.container_number) LIKE LOWER(CONCAT('%', :#{#request.container_number}, '%'))) " +
             "AND (:#{#request.iso_code} IS NULL OR LOWER(inv.iso_code) LIKE LOWER(CONCAT('%', :#{#request.iso_code}, '%'))) " +
             "AND (:#{#request.condition} IS NULL OR LOWER(inv.condition) LIKE LOWER(CONCAT('%', :#{#request.condition}, '%'))) " +
             "AND (:#{#request.clean} IS NULL OR inv.clean = :#{#request.clean}) " +
             "AND (:#{#request.grade} IS NULL OR LOWER(inv.grade) LIKE LOWER(CONCAT('%', :#{#request.grade}, '%'))) " +
             "AND (:#{#request.damage_by} IS NULL OR LOWER(inv.damage_by) LIKE LOWER(CONCAT('%', :#{#request.damage_by}, '%'))) " +
             "AND (:#{#request.discharge_port} IS NULL OR LOWER(inv.discharge_port) LIKE LOWER(CONCAT('%', :#{#request.discharge_port}, '%'))) " +
             "AND (:#{#request.date_manufacturer} IS NULL OR LOWER(inv.date_manufacturer) LIKE LOWER(CONCAT('%', :#{#request.date_manufacturer}, '%'))) " +
             "AND (:#{#request.globalSearch} IS NULL " +
             "OR LOWER(inv.depot) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.fleet_manager) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.container_number) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.iso_code) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.condition) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.grade) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.damage_by) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.discharge_port) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             "OR LOWER(inv.date_manufacturer) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
             ")";

    @Query(value = "SELECT inv FROM Inventory inv WHERE inv.depoOwnerAccount.companyEmail = :companyEmail AND inv.gateOutId IS NULL")
    Page<Inventory> getAllInventory (@Param("companyEmail") String companyEmail, Pageable pageable);

    @Query(value = "SELECT * FROM inventories " +
            "WHERE inventories.depo_owner_account_id = :depoId " +
            "AND inventories.container_number = :containerNo " +
            "AND inventories.gate_out_id IS NULL",
            nativeQuery = true)
    List<Inventory> findTopByContainerNumber (@Param("depoId") Long depoId,
                                              @Param("containerNo") String containerNumber);
    @Query(value = SEARCH_INVETORY_QUERY)
    Page<Inventory> searchByCondition(@Param("companyEmail") String companyEmail,
                                      @Param("request") InventorySearchRequest request,
                                      Pageable pageable);
}
