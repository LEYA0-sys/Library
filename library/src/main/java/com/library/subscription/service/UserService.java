package com.library.subscription.service;

import com.library.subscription.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 获取用户列表
     * 
     * @param keyword 关键字
     * @return 用户列表
     */
    List<User> getUserList(String keyword);

    /**
     * 根据用户名和密码获取管理员用户
     * 
     * @param user 用户对象
     * @return 管理员用户
     */
    User getAdminUser(User user);

    /**
     * 根据用户名和密码获取普通用户
     * 
     * @param user 用户对象
     * @return 普通用户
     */
    User getNormalUser(User user);

    /**
     * 创建用户
     * 
     * @param user 用户对象
     */
    void createUser(User user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteUser(String id);

    /**
     * 更新用户
     * 
     * @param user 用户对象
     */
    void updateUser(User user);

    /**
     * 根据token获取用户
     * 
     * @param token token
     * @return 用户对象
     */
    User getUserByToken(String token);

    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户对象
     */
    User getUserByUsername(String username);

    /**
     * 获取用户详情
     * 
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserDetail(String userId);

    /**
     * 修改用户密码
     * 
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);
}