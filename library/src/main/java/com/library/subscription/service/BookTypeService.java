package com.library.subscription.service;

import com.library.subscription.entity.BookType;

import java.util.List;

/**
 * 书籍类型服务接口
 */
public interface BookTypeService {
    /**
     * 获取类型列表
     * @return 类型列表
     */
    List<BookType> getTypeList();
    
    /**
     * 创建类型
     * @param bookType 类型对象
     */
    void createType(BookType bookType);
    
    /**
     * 删除类型
     * @param id 类型ID
     */
    void deleteType(String id);
    
    /**
     * 更新类型
     * @param bookType 类型对象
     */
    void updateType(BookType bookType);
    
    /**
     * 根据ID获取类型
     * @param id 类型ID
     * @return 类型对象
     */
    BookType getTypeById(String id);
} 