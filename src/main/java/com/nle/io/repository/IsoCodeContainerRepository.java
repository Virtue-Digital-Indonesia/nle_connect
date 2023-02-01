package com.nle.io.repository;

import com.nle.io.entity.IsoCodeContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IsoCodeContainerRepository extends JpaRepository<IsoCodeContainer, Long> {

    @Override
    @Query(value = "SELECT * FROM iso_container_codes", nativeQuery = true)
    Page<IsoCodeContainer> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM iso_container_codes where iso_container_codes.iso_code = :code", nativeQuery = true)
    Optional<IsoCodeContainer> findByCode(String code);
}
