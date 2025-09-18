package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.User;
import com.library.subscription.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 登录控制器
 */
@Api(tags = "登录管理")
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @ApiOperation("管理员登录")
    @PostMapping("/admin/login")
    public R<Map<String, Object>> adminLogin(@RequestBody User user) {
        User adminUser = userService.getAdminUser(user);
        if (adminUser == null) {
            return R.error("用户名或密码错误");
        }
        
        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        adminUser.setPassword(null); // 不返回密码
        data.put("user", adminUser);
        
        return R.success(data);
    }
    
    @ApiOperation("普通用户登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody User user) {
        User normalUser = userService.getNormalUser(user);
        if (normalUser == null) {
            return R.error("用户名或密码错误");
        }
        
        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        normalUser.setPassword(null); // 不返回密码
        data.put("user", normalUser);
        
        return R.success(data);
    }
    
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R<Void> register(@RequestBody User user) {
        // 检查用户名是否已存在
        User existUser = userService.getUserByUsername(user.getUsername());
        if (existUser != null) {
            return R.error("用户名已存在");
        }
        
        userService.createUser(user);
        return R.success();
    }
    
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        return R.success();
    }
} 