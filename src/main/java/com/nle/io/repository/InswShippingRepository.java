package com.nle.io.repository;

import com.nle.io.entity.Fleet;
import com.nle.io.entity.InswShipping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InswShippingRepository extends JpaRepository<InswShipping, Long> {

    @Override
    @Query(value = "SELECT * FROM insw_shipping", nativeQuery = true)
    Page<InswShipping> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM insw_shipping where insw_shipping.shipping_code = :code", nativeQuery = true)
    Optional<InswShipping> findByCode(String code);

    @Query(value = "SELECT * FROM insw_shipping", nativeQuery = true)
    Page<Fleet> getAllShipping(Pageable pageable);

}
