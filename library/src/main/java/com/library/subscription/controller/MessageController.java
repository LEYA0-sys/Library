package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.Message;
import com.library.subscription.service.MessageService;
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
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("获取留言列表")
    @GetMapping("/list")
    public R<List<Message>> list() {
        List<Message> list = messageService.getMessageList();
        return R.success(list);
    }

    @ApiOperation("获取留言详情")
    @GetMapping("/detail/{id}")
    public R<Message> detail(@PathVariable String id) {
        Message message = messageService.getMessageDetail(id);
        return R.success(message);
    }

    @ApiOperation("创建留言")
    @PostMapping("/create")
    public R<Void> create(@RequestBody Message message) {
        messageService.createMessage(message);
        return R.success();
    }

    @ApiOperation("更新留言")
    @PostMapping("/update")
    public R<Void> update(@RequestBody Message message) {
        messageService.updateMessage(message);
        return R.success();
    }

    @ApiOperation("删除留言")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        messageService.deleteMessage(id);
        return R.success();
    }
    
    @ApiOperation("获取用户留言列表")
    @GetMapping("/user/{userId}")
    public R<List<Message>> getUserMessages(@PathVariable String userId) {
        List<Message> list = messageService.getUserMessageList(userId);
        return R.success(list);
    }
} 