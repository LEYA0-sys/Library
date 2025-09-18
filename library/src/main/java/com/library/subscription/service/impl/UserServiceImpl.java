package com.library.subscription.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.subscription.entity.User;
import com.library.subscription.mapper.UserMapper;
import com.library.subscription.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getUserList(String keyword) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getName, keyword)
                    .or()
                    .like(User::getMobile, keyword);
        }
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public User getAdminUser(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword())
                .eq(User::getRole, "admin");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getNormalUser(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword())
                .eq(User::getRole, "user");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void createUser(User user) {
        // 设置默认值
        if (user.getReadingCount() == null) {
            user.setReadingCount(0);
        }
        if (user.getCredit() == null) {
            user.setCredit(100);
        }
        if (StringUtils.isBlank(user.getStatus())) {
            user.setStatus("active");
        }
        if (StringUtils.isBlank(user.getRole())) {
            user.setRole("user");
        }

        // 设置创建和更新时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        user.setCreateTime(now);
        user.setUpdateTime(now);

        userMapper.insert(user);
    }

    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(Long.valueOf(id));
    }

    @Override
    public void updateUser(User user) {
        // 设置更新时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        user.setUpdateTime(now);

        System.out.println("准备更新用户: " + user);

        // 如果传入的用户信息不完整，需要先查询现有信息
        if (user.getUsername() == null || user.getPassword() == null) {
            User existingUser = userMapper.selectById(user.getId());
            if (existingUser != null) {
                // 保留原有值
                if (user.getUsername() == null) {
                    user.setUsername(existingUser.getUsername());
                }
                if (user.getPassword() == null) {
                    user.setPassword(existingUser.getPassword());
                }
                if (user.getName() == null) {
                    user.setName(existingUser.getName());
                }
                if (user.getRole() == null) {
                    user.setRole(existingUser.getRole());
                }
                if (user.getStatus() == null) {
                    user.setStatus(existingUser.getStatus());
                }
                if (user.getReadingCount() == null) {
                    user.setReadingCount(existingUser.getReadingCount());
                }
                if (user.getCredit() == null) {
                    user.setCredit(existingUser.getCredit());
                }
            }
        }

        userMapper.updateById(user);
        System.out.println("用户更新完成: " + user.getId());
    }

    @Override
    public User getUserByToken(String token) {
        // 由于实体中没有token字段，此处使用username作为临时替代
        // 实际项目中应添加token字段或使用其他认证方式
        return getUserByUsername(token);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserDetail(String userId) {
        return userMapper.selectById(Long.valueOf(userId));
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        // 查询用户
        User user = getUserDetail(userId);
        if (user == null) {
            return false;
        }

        // 验证旧密码
        if (!oldPassword.equals(user.getPassword())) {
            return false;
        }

        // 更新密码
        user.setPassword(newPassword);
        // 设置更新时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        user.setUpdateTime(now);

        userMapper.updateById(user);
        return true;
    }
}