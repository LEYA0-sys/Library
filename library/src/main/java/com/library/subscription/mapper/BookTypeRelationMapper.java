package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.BookTypeRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书籍类型关联Mapper接口
 */
@Mapper
public interface BookTypeRelationMapper extends BaseMapper<BookTypeRelation> {
} 