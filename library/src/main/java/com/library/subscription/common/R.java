package com.library.subscription.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应对象
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code; // 状态码
    private String message; // 消息
    private T data; // 数据

    private R() {
    }

    /**
     * 成功返回结果
     */
    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("操作成功");
        return r;
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        return r;
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param message 提示信息
     */
    public static <T> R<T> success(T data, String message) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    /**
     * 失败返回结果
     *
     * @param code 状态码
     * @param message 提示信息
     */
    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
} 