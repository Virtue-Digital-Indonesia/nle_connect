package com.nle.io.repository;

import com.nle.io.entity.order.OrderHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderHeader, Long> {

    @Query(value = "SELECT * FROM order_header WHERE order_header.phone_number = :phoneNumber AND order_status != DONE", nativeQuery = true)
    Page<OrderHeader> getOrderByPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

}
