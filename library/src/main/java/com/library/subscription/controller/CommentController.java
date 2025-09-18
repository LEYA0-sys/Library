package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.Comment;
import com.library.subscription.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言控制器
 */
@Api(tags = "留言管理")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("获取留言列表")
    @GetMapping("/list")
    public R<List<Comment>> list() {
        List<Comment> list = commentService.getCommentList();
        return R.success(list);
    }

    @ApiOperation("创建留言")
    @PostMapping("/create")
    public R<Void> create(@RequestBody Comment comment) {
        commentService.createComment(comment);
        return R.success();
    }

    @ApiOperation("删除留言")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        commentService.deleteComment(id);
        return R.success();
    }
}