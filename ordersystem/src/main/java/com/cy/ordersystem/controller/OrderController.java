package com.cy.ordersystem.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.cy.ordersystem.entity.*;
import com.cy.ordersystem.repository.OrderRepository;
import com.cy.ordersystem.repository.StepRepository;
import com.cy.ordersystem.repository.UserRepository;
import com.cy.ordersystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


import javax.swing.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private UserRepository userRepository;

    private static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMms5LhigiD5JnkK\n" +
            "OJltKBzp3fys/FPqqGeSdjJz/BXcNTT9THLyLrkY5bB5uhp3SkeI/Nrjn70I7Sya\n" +
            "UwvweDDlTGgrLmnIgan0rkbAonoI137HgfkpJqK5P9lIdQvgaISw/Nd0BhD63Xbt\n" +
            "oypfY7ybYgquPj5JBirwurMaParjAgMBAAECgYEAoKiKzBgtcfcJHwN1c0Px4Jat\n" +
            "sMxddrxIy7lxT1/2QbPa7wuaCzfC4NFkqOFP0CWXlsUaKYWLixvBVPbbkXdPRrlg\n" +
            "x7sU/3wsXBP9gevdL0qAQB7LBr2sLqD+Ksb9Ntgvl6FWy6Jag20IbjFnLygbGPvm\n" +
            "PrM/GuoLCRwOtZh0roECQQD0qruxiWETdltR1Ke3txe+wV8/BaXDkWLszgeaHEmD\n" +
            "vPbbcUcU4II/Xh7PanUEyoeVAgliCwURWVc6xXs9auQFAkEA0wRdfal0Q4ohDURe\n" +
            "u+FxoQRrKznYzorBG2Lu62yeezx2SwAp3THErDgNOS8OP1L79BlDyAN0A6FHUbtj\n" +
            "ctuvxwJBAIKvx73XAbFhoPmeu09YTyinJXcGTzqdJ9gRXOImheYGQ12Hrk4qdaff\n" +
            "YFmuJX8/pxgilFx4Qma37+4GmgN+WvkCQHV7Pa3DlRYxJfuj7/Y8Kq4RrrTPLPS4\n" +
            "7ZAYwZnSzF0iKV3SRnx+w9d6EWqf1xfMVLfvBPyrZ4DJ3tITOOfuhWsCQGJUCA6g\n" +
            "23TSGpsY75+e+67TW4GqKePwAc6slfYDDjksgNwpOaYpYYHwFYBXXKFy2sG2c/4Z\n" +
            "uBRfOmIa3Zn5j1A=";

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

    @PostMapping("/register")
    public Result register(@RequestBody User user) {

        RSA rsa = new RSA(PRIVATE_KEY, null);
        String decrypt_username = rsa.decryptStr(user.getUsername(), KeyType.PrivateKey);
        String decrypt_password = rsa.decryptStr(user.getPassword(), KeyType.PrivateKey);

        if (StrUtil.isBlank(decrypt_username) || StrUtil.isBlank(decrypt_password)) {
            return new Result(1, "用户名密码不能为空!");
        }
        if (decrypt_password.length() < 6) {
            return new Result(1, "密码不能少于6位");
        }
        List<User> userList = userRepository.findByUsername(decrypt_username);
        if (!userList.isEmpty()) {
            return new Result(1, "用户名已存在，请更换用户名!");
        }
        User decrypt_user = new User();
        decrypt_user.setUsername(decrypt_username);
        decrypt_user.setPassword(decrypt_password);
        User user1 = userRepository.saveAndFlush(decrypt_user);
        if (user1.getId() > 0) {
            return new Result(0, "注册成功!");
        }
        return new Result(1, "注册失败!");
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        RSA rsa = new RSA(PRIVATE_KEY, null);
        String decrypt_username = rsa.decryptStr(user.getUsername(), KeyType.PrivateKey);
        String decrypt_password = rsa.decryptStr(user.getPassword(), KeyType.PrivateKey);
        List<User> userList = userRepository.findByUsernameAndPassword(decrypt_username, decrypt_password);
        if (!userList.isEmpty()) {
            String token = JwtUtil.generateToken(userList.get(0).getUsername(), userList.get(0).getPassword());
            userRepository.updateToken(token, decrypt_username);
            return new Result(0, "登录成功!", token);
        }
        return new Result(1, "用户名或密码错误！");
    }

    @PostMapping("/logout")
    public Result logout(@RequestBody User user) {
        // 退出登录，清除token
        userRepository.updateToken("", user.getUsername());
        return new Result(0, "已退出登录!");
    }

    @PostMapping("/modifyPassword")
    public Result modifyPassword(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String newPassword = map.get("newPassword");

        RSA rsa = new RSA(PRIVATE_KEY, null);
        String decrypt_username = rsa.decryptStr(username, KeyType.PrivateKey);
        String decrypt_password = rsa.decryptStr(password, KeyType.PrivateKey);
        String decrypt_newPassword = rsa.decryptStr(newPassword, KeyType.PrivateKey);

        if (StrUtil.isBlank(decrypt_username) || StrUtil.isBlank(decrypt_password) || StrUtil.isBlank(decrypt_newPassword)) {
            return new Result(1, "密码不能为空");
        }
        if (decrypt_newPassword.length() < 6) {
            return new Result(1, "密码不能少于6位");
        }
        List<User> userList = userRepository.findByUsernameAndPassword(decrypt_username, decrypt_password);
        if (userList.isEmpty()) {
            return new Result(1, "旧密码不正确");
        }
        userRepository.updatePassword(decrypt_newPassword, decrypt_username);
        return new Result(0, "修改成功，请重新登录!");
    }

}
