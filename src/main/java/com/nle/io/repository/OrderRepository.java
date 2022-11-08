package com.nle.io.repository;

import com.nle.io.entity.order.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderHeader, Long> {

}
