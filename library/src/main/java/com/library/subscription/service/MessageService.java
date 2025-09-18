package com.library.subscription.service;

import com.library.subscription.entity.Message;

import java.util.List;

/**
 * 留言服务接口
 */
public interface MessageService {
    /**
     * 获取留言列表
     * @return 留言列表
     */
    List<Message> getMessageList();
    
    /**
     * 创建留言
     * @param message 留言对象
     */
    void createMessage(Message message);
    
    /**
     * 删除留言
     * @param id 留言ID
     */
    void deleteMessage(String id);
    
    /**
     * 更新留言
     * @param message 留言对象
     */
    void updateMessage(Message message);
    
    /**
     * 获取留言详情
     * @param id 留言ID
     * @return 留言对象
     */
    Message getMessageDetail(String id);
    
    /**
     * 获取用户留言列表
     * @param userId 用户ID
     * @return 留言列表
     */
    List<Message> getUserMessageList(String userId);
} 