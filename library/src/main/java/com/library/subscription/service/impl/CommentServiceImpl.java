package com.library.subscription.service.impl;

import com.library.subscription.entity.Comment;
import com.library.subscription.mapper.CommentMapper;
import com.library.subscription.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 留言服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> getCommentList() {
        return commentMapper.getCommentWithUser();
    }

    @Override
    public void createComment(Comment comment) {
        // 设置留言时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        comment.setCommentTime(now);

        commentMapper.insert(comment);
    }

    @Override
    public void deleteComment(String id) {
        commentMapper.deleteById(Long.valueOf(id));
    }
}