package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书Mapper接口
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
    /**
     * 获取带有分类信息的图书
     * 
     * @param id 图书ID
     * @return 带有分类信息的图书
     */
    Book getBookWithTypes(Long id);

    /**
     * 根据关键字搜索书籍
     * 
     * @param keyword 关键字
     * @return 书籍列表
     */
    List<Book> searchBooks(@Param("keyword") String keyword);

    /**
     * 获取所有带分类信息的图书列表
     * 
     * @param keyword 搜索关键词（可选）
     * @return 带有分类信息的图书列表
     */
    List<Book> getBookListWithTypes(@Param("keyword") String keyword);

    /**
     * 根据分类ID获取带分类信息的图书列表
     * 
     * @param typeId 分类ID
     * @return 带有分类信息的图书列表
     */
    List<Book> getBooksByTypeWithTypes(@Param("typeId") Long typeId);
}