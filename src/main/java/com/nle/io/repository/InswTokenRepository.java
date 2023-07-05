package com.nle.io.repository;

import com.nle.io.entity.InswToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface InswTokenRepository extends JpaRepository<InswToken, Long> {
    InswToken findByActiveStatus(String activeStatus);

    @Modifying
    @Query(value = "update InswToken it set it.activeStatus = :activeStatus where it.id = :id")
    int updateStatus(@Param("activeStatus") String activeStatus, @Param("id") Long id);
}
