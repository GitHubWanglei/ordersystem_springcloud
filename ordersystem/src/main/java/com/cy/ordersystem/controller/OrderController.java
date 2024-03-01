package com.cy.ordersystem.controller;

import com.cy.ordersystem.entity.*;
import com.cy.ordersystem.repository.OrderRepository;
import com.cy.ordersystem.repository.StepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StepRepository stepRepository;

    @GetMapping("/order/getAll")
    public Result getAllOrder(@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo, Integer pageSize) {

        List<Order> orderList = orderRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending())).getContent();
        for (int i = 0; i < orderList.size(); i++) {
            List<Step> stepList = stepRepository.findAllByOrderId(orderList.get(i).getOrderId());
            orderList.get(i).setStepList(stepList);
        }
        Long count = orderRepository.count();
        PageResult result = new PageResult();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotal(count);
        result.setDataList(orderList);
        return new Result(0, "查询成功！", result);
    }

    @GetMapping("/order/getOrderByOrderId")
    public Result getOrderByOrderId(@RequestParam(value = "orderId", defaultValue = "") String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            return new Result(1, "订单号不能为空!");
        }
        List<Order> orderList = orderRepository.findOrdersByOrderId(orderId);
        for (int i = 0; i < orderList.size(); i++) {
            List<Step> stepList = stepRepository.findAllByOrderId(orderList.get(i).getOrderId());
            orderList.get(i).setStepList(stepList);
        }
        Result result = new Result();
        result.setCode(0);
        result.setMessage(orderList.isEmpty() ? "未查询到此订单！": "查询成功！");
        if (!orderList.isEmpty()) {
            result.setData(orderList.get(0));
        }
        return result;
    }

    @PostMapping("order/save")
    public Result save(@RequestBody Order order) {
        System.out.println(order);
        List<Order> orderList = orderRepository.findOrdersByOrderId(order.getOrderId());
        if (!orderList.isEmpty()) {
            return new Result(1, "订单号"+order.getOrderId()+"已存在，不可重复添加!");
        }
        Order order1 = orderRepository.saveAndFlush(order);
        stepRepository.saveAllAndFlush(order.getStepList());

        if (order1.getId() > 0) {
            return new Result(0, "保存成功");
        } else {
            return new Result(1, "保存失败");
        }
    }

    @PostMapping("/order/delete")
    public Result delete(@RequestBody Map<String, String> map) {
        String orderId = map.get("orderId");
        if (orderId == null ||orderId.isEmpty()) {
            return new Result(1, "orderId不能为空!");
        }
        List<Order> orderList = orderRepository.deleteByOrderId(orderId);
        stepRepository.deleteByOrderId(orderId);
        if (!orderList.isEmpty()) {
            return new Result(0, "删除成功!");
        }
        return new Result(1, "删除失败!");
    }

    @PostMapping("order/update")
    public Result update(@RequestBody Order order) {
        System.out.println(order);
        Order order1 = orderRepository.saveAndFlush(order);
        stepRepository.saveAllAndFlush(order.getStepList());

        if (order1.getId() > 0) {
            return new Result(0, "修改成功！");
        } else {
            return new Result(1, "修改失败！");
        }
    }

}
