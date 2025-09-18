package com.library.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 留言实体类
 */
@Data
@TableName("bs_message")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("content")
    private String content;
    
    @TableField("message_time")
    private String messageTime;
    
    @TableField("create_time")
    private String createTime;
    
    @TableField(exist = false)
    private String username; // 用户名
} 