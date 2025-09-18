package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.User;
import com.library.subscription.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public R<List<User>> list(@RequestParam(required = false) String keyword) {
        List<User> list = userService.getUserList(keyword);
        return R.success(list);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/detail/{id}")
    public R<User> detail(@PathVariable String id) {
        User user = userService.getUserDetail(id);
        return R.success(user);
    }

    @ApiOperation("创建用户")
    @PostMapping("/create")
    public R<Void> create(@RequestBody User user) {
        userService.createUser(user);
        return R.success();
    }

    @ApiOperation("更新用户")
    @PostMapping("/update")
    public R<Void> update(@RequestBody User user) {
        System.out.println("接收到更新用户请求: " + user);
        userService.updateUser(user);
        return R.success();
    }

    @ApiOperation("删除用户")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return R.success();
    }

    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    public R<Void> changePassword(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        String oldPassword = params.get("oldPassword").toString();
        String newPassword = params.get("newPassword").toString();

        // 验证旧密码是否正确
        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        if (!success) {
            return R.error("原密码不正确");
        }
        return R.success();
    }
}