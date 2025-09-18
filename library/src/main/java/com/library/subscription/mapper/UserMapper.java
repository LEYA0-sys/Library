package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 用户搜索
     * @param keyword 关键字
     * @return 用户列表
     */
    List<User> searchUsers(@Param("keyword") String keyword);
    
    /**
     * 获取用户统计信息
     * @return 统计信息
     */
    Map<String, Object> getUserStats();
} 