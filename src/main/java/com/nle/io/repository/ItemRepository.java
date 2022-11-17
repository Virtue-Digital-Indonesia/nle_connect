package com.nle.io.repository;

import com.nle.constant.enums.ItemTypeEnum;
import com.nle.io.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT it FROM Item it WHERE it.depoOwnerAccount.companyEmail = :companyEmail AND it.deleted = false")
    Page<Item> getAllDepoItem(@Param("companyEmail") String companyEmail, Pageable pageable);

    @Query(value = "SELECT it FROM Item it WHERE it.depoOwnerAccount.id = :depo_id AND it.type = :type AND it.deleted = false")
    List<Item> getAllByDepoId(@Param("depo_id") Long depo_id, ItemTypeEnum type);

    @Query(value = "SELECT it FROM Item it " +
            "LEFT JOIN it.depoFleet as dF " +
            "LEFT JOIN dF.fleet as f " +
            "WHERE it.depoOwnerAccount.companyEmail = :companyEmail " +
            "AND (:itemName is null OR (lower(it.item_name) like lower(concat('%',:itemName,'%')))) " +
            "AND (:sku is null OR (lower(it.sku) like lower(concat('%',:sku,'%')))) " +
            "AND (:description is null OR (lower(it.description ) like lower(concat('%',:description,'%')))) " +
            "AND (:type is null OR (lower(it.type) like(concat('%',:type,'%')))) " +
            "AND (:price is null OR (it.price= :price)) " +
            "AND (:deleted is null OR (it.deleted= :deleted)) " +
            "AND (:fleetCode is null OR (lower(f.code) like lower(concat('%',:fleetCode,'%')))) " +
            "AND (:fleetName is null OR(lower(dF.name) like lower(concat('%',:fleetName,'%')))) " +
            "AND (:status is null OR (it.status = :status)) " +
            "AND (:globalSearch is null " +
            "       OR (lower(it.item_name) like lower(concat('%',:globalSearch,'%')))" +
            "       OR (lower(it.sku) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR (lower(it.description ) like(concat('%',:globalSearch,'%'))) " +
            "       OR (lower(it.type) like(concat('%',:globalSearch,'%'))) " +
            "       OR (lower(f.code) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR(lower(dF.name) like lower(concat('%',:globalSearch,'%')))" +
            ") ")
    Page<Item> searchItem(String companyEmail,
                          String itemName,
                          String sku,
                          String description,
                          String fleetName,
                          Integer price,
                          String type,
                          Boolean deleted,
                          Boolean status,
                          String fleetCode,
                          String globalSearch,
                          Pageable pageable);
}
