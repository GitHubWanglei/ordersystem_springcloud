package com.cy.ordersystem.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.cy.ordersystem.entity.Result;
import com.cy.ordersystem.entity.User;
import com.cy.ordersystem.repository.UserRepository;
import com.cy.ordersystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

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

    @PostMapping("/register")
    public Result register(@RequestBody User user) {

        RSA rsa = new RSA(PRIVATE_KEY, null);
        String decrypt_username = rsa.decryptStr(user.getUsername(), KeyType.PrivateKey);
        String decrypt_password = rsa.decryptStr(user.getPassword(), KeyType.PrivateKey);

        if (StrUtil.isBlank(decrypt_username) || StrUtil.isBlank(decrypt_password)) {
            return new Result(1, "用户名或密码不能为空!");
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
            return new Result(1, "用户名或密码不能为空");
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
