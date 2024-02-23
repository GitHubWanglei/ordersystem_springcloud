package com.cy.ordersystem.entity;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    Integer pageNo;
    Integer pageSize;
    Long total;
    List<Order> dataList;
}
