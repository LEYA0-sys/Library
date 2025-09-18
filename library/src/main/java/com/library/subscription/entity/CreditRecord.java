package com.library.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 信用记录实体类
 */
@Data
@TableName("bs_credit_record")
public class CreditRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("credit_change")
    private Integer creditChange;

    @TableField("reason")
    private String reason;

    @TableField("change_time")
    private String changeTime;

    @TableField(exist = false)
    private String username; // 用户名，非数据库字段
}