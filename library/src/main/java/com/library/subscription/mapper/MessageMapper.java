package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 留言Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    /**
     * 获取留言列表
     * @return 留言列表
     */
    List<Message> getMessageList();
    
    /**
     * 获取留言详情
     * @param id 留言ID
     * @return 留言对象
     */
    Message getMessageDetail(@Param("id") Long id);
    
    /**
     * 获取用户留言列表
     * @param userId 用户ID
     * @return 留言列表
     */
    List<Message> getUserMessageList(@Param("userId") Long userId);
} 