package com.library.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体类
 */
@Data
@TableName("bs_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("name")
    private String name;

    @TableField("mobile")
    private String mobile;

    @TableField("email")
    private String email;

    @TableField("reading_count")
    private Integer readingCount;

    @TableField("credit")
    private Integer credit;

    @TableField("role")
    private String role;

    @TableField("status")
    private String status;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;
}