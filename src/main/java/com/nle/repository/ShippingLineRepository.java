package com.nle.repository;

import com.nle.entity.ShippingLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the ShippingLine entity.
 */
@Repository
public interface ShippingLineRepository extends JpaRepository<ShippingLine, Long> {
    Optional<ShippingLine> findByCode(String code);
}
