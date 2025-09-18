package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.Borrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借阅记录Mapper接口
 */
@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {
    /**
     * 获取借阅详情
     * @param id 借阅ID
     * @return 借阅对象
     */
    Borrow getBorrowDetail(@Param("id") Long id);
    
    /**
     * 获取用户的借阅列表
     * @param userId 用户ID
     * @param status 借阅状态
     * @return 借阅列表
     */
    List<Borrow> getUserBorrowList(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 获取借阅列表
     * @param keyword 关键字
     * @return 借阅列表
     */
    List<Borrow> getBorrowList(@Param("keyword") String keyword);
} 