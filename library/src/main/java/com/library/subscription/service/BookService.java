package com.library.subscription.service;

import com.library.subscription.entity.Book;

import java.util.List;

/**
 * 书籍服务接口
 */
public interface BookService {
    /**
     * 获取书籍列表
     * @param keyword 关键字
     * @return 书籍列表
     */
    List<Book> getBookList(String keyword);
    
    /**
     * 创建书籍
     * @param book 书籍对象
     */
    void createBook(Book book);
    
    /**
     * 删除书籍
     * @param id 书籍ID
     */
    void deleteBook(String id);
    
    /**
     * 更新书籍
     * @param book 书籍对象
     */
    void updateBook(Book book);
    
    /**
     * 根据ID获取书籍详情
     * @param id 书籍ID
     * @return 书籍对象
     */
    Book getBookDetail(String id);
    
    /**
     * 获取指定类型的书籍列表
     * @param typeId 类型ID
     * @return 书籍列表
     */
    List<Book> getBooksByType(String typeId);
} 