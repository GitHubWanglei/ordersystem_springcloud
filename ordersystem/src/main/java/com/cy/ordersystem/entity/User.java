package com.cy.ordersystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键id
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String token;
}
