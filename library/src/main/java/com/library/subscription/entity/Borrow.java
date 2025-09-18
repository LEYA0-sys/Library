package com.library.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 借阅记录实体类
 */
@Data
@TableName("bs_borrow")
public class Borrow implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("book_id")
    private Long bookId;
    
    @TableField("start_time")
    private String startTime;
    
    @TableField("end_time")
    private String endTime;
    
    @TableField("expect_time")
    private String expectTime;
    
    @TableField("borrow_status")
    private String borrowStatus;
    
    @TableField("has_delayed")
    private String hasDelayed;
    
    @TableField("create_time")
    private String createTime;
    
    @TableField("update_time")
    private String updateTime;
    
    @TableField(exist = false)
    private String username; // 用户名
    
    @TableField(exist = false)
    private String bookName; // 书籍名称
    
    @TableField(exist = false)
    private String cover; // 书籍封面
} 