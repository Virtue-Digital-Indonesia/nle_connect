package com.nle.io.repository;

import com.nle.io.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data SQL repository for the Inventory entity.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
