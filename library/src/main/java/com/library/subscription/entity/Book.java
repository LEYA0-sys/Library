package com.library.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 书籍实体类
 */
@Data
@TableName("bs_book")
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("book_name")
    private String bookName;

    @TableField("author")
    private String author;

    @TableField("book_code")
    private String bookCode;

    @TableField("status")
    private String status;

    @TableField("borrow_count")
    private Integer borrowCount;

    @TableField("user_id")
    private Long userId;

    @TableField("cover")
    private String cover;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;

    @TableField(exist = false)
    private List<BookType> bookTypes; // 书籍类型

    @TableField(exist = false)
    private List<Long> bookTypeIds; // 前端传递的书籍类型ID
}