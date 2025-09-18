package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 留言Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 获取带有用户信息的留言列表
     * 
     * @return 留言列表
     */
    List<Comment> getCommentWithUser();
}