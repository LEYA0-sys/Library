package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.BookType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 书籍类型Mapper接口
 */
@Mapper
public interface BookTypeMapper extends BaseMapper<BookType> {
    /**
     * 获取带有书籍数量的类型列表
     * @return 类型列表
     */
    List<Map<String, Object>> getTypeWithBookCount();
} 