package com.library.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 书籍类型关联实体类
 */
@Data
@TableName("bs_book_type_relation")
public class BookTypeRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("book_id")
    private Long bookId;
    
    @TableField("type_id")
    private Long typeId;
    
    @TableField("create_time")
    private String createTime;
} 