package com.nle.io.repository.order;

import com.nle.io.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> getAllByOrderHeaderId(Long headerId);
}
