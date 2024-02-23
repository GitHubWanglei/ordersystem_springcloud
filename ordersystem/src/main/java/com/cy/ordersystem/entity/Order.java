package com.cy.ordersystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键id
    @Column
    private String orderId; // 订单id
    @Column
    private String deviceName; // 设备名称
    @Column
    private String  salesMan; // 业务员
    @Column
    private String area; // 地区
    @Column
    private String designer; // 设计员
    @Column
    private Integer number; // 数量
    @Column
    private Long orderDate; // 下单日期
    @Column
    private Long deliveryDate; // 交货日期
    @Column
    private Long completionDate; // 完工日期
    @Column
    private Long shipmentDate; // 发货日期
    @Transient
    private List<Step> stepList; // 节点列表
}
