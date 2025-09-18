package com.library.subscription.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.subscription.entity.Message;
import com.library.subscription.mapper.MessageMapper;
import com.library.subscription.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> getMessageList() {
        return messageMapper.selectList(null);
    }

    @Override
    public void createMessage(Message message) {
        // 设置创建时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        message.setCreateTime(now);
        message.setMessageTime(now);
        
        messageMapper.insert(message);
    }

    @Override
    public void deleteMessage(String id) {
        messageMapper.deleteById(Long.valueOf(id));
    }
    
    @Override
    public void updateMessage(Message message) {
        messageMapper.updateById(message);
    }
    
    @Override
    public Message getMessageDetail(String id) {
        return messageMapper.selectById(Long.valueOf(id));
    }
    
    @Override
    public List<Message> getUserMessageList(String userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getUserId, Long.valueOf(userId))
                .orderByDesc(Message::getCreateTime);
        return messageMapper.selectList(queryWrapper);
    }
}