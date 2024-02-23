package com.cy.ordersystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_step")
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键id
    @Column
    private Integer sequence; // 次序
    @Column
    private String orderId; // 订单号
    @Column
    private String name; // 节点名称
    @Column
    private Long planStartDate; // 计划开始时间
    @Column
    private Long planEndDate; // 计划结束时间
    @Column
    private Long endDate; // 实际结束时间
    @Column
    private String timeoutReason; // 超期原因
}
