package com.cy.ordersystem.repository;

import com.cy.ordersystem.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByOrderId(String orderId);

    @Transactional
    List<Order> deleteByOrderId(String orderId);

}
