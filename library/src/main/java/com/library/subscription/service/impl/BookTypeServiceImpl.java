package com.library.subscription.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.subscription.entity.BookType;
import com.library.subscription.mapper.BookTypeMapper;
import com.library.subscription.service.BookTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 图书类型服务实现类
 */
@Service
public class BookTypeServiceImpl implements BookTypeService {

    @Autowired
    private BookTypeMapper bookTypeMapper;

    @Override
    public List<BookType> getTypeList() {
        return bookTypeMapper.selectList(null);
    }

    @Override
    public void createType(BookType bookType) {
        // 设置创建时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        bookType.setCreateTime(now);
        
        bookTypeMapper.insert(bookType);
    }

    @Override
    public void deleteType(String id) {
        bookTypeMapper.deleteById(Long.valueOf(id));
    }

    @Override
    public void updateType(BookType bookType) {
        bookTypeMapper.updateById(bookType);
    }

    @Override
    public BookType getTypeById(String id) {
        return bookTypeMapper.selectById(Long.valueOf(id));
    }
} 