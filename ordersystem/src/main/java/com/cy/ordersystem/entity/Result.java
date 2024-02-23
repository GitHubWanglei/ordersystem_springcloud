package com.cy.ordersystem.entity;

import lombok.Data;

@Data
public class Result {
    // 0:成功，1:失败，2:未登录或登录已过期
    private Integer code;
    private String message;
    private Object data;

    public Result() {}

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
