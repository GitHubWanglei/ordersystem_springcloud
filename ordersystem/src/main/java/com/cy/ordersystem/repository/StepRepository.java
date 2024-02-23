package com.cy.ordersystem.repository;

import com.cy.ordersystem.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {

    List<Step> findAllByOrderId(String orderId);

    @Transactional
    void deleteByOrderId(String orderId);

}
