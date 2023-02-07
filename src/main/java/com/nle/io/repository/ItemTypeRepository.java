package com.nle.io.repository;

import com.nle.io.entity.ItemType;
import com.nle.ui.model.response.ItemTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {

    @Override
    @Query(value = "SELECT * FROM item_type_name", nativeQuery = true)
    Page<ItemType> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM item_type_name where item_type_name.item_code = :code", nativeQuery = true)
    Optional<ItemType> findByCode(String code);

    @Query(value = "SELECT * FROM item_type_name where item_type_name.item_code like %:code%", nativeQuery = true)
    List<ItemType> findByItemCode(String code);
}
