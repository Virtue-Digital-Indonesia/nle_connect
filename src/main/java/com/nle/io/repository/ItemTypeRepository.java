package com.nle.io.repository;

import com.nle.io.entity.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {

    @Override
    @Query(value = "SELECT * FROM item_type_name", nativeQuery = true)
    Page<ItemType> findAll(Pageable pageable);
}
