package com.library.subscription.service;

import com.library.subscription.entity.Comment;

import java.util.List;

/**
 * 留言服务接口
 */
public interface CommentService {
    /**
     * 获取留言列表
     * 
     * @return 留言列表
     */
    List<Comment> getCommentList();

    /**
     * 创建留言
     * 
     * @param comment 留言对象
     */
    void createComment(Comment comment);

    /**
     * 删除留言
     * 
     * @param id 留言ID
     */
    void deleteComment(String id);
}