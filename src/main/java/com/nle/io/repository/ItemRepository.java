package com.nle.io.repository;

import com.nle.io.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT it FROM Item it WHERE it.depoOwnerAccount.companyEmail = :companyEmail AND it.deleted = false")
    Page<Item> getAllDepoItem(@Param("companyEmail") String companyEmail, Pageable pageable);
}
