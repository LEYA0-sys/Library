package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.BookType;
import com.library.subscription.service.BookTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书类型控制器
 */
@Api(tags = "图书类型管理")
@RestController
@RequestMapping("/bookType")
public class BookTypeController {

    @Autowired
    private BookTypeService bookTypeService;

    @ApiOperation("获取类型列表")
    @GetMapping("/list")
    public R<List<BookType>> list() {
        List<BookType> list = bookTypeService.getTypeList();
        return R.success(list);
    }

    @ApiOperation("获取类型详情")
    @GetMapping("/detail/{id}")
    public R<BookType> detail(@PathVariable String id) {
        BookType bookType = bookTypeService.getTypeById(id);
        return R.success(bookType);
    }

    @ApiOperation("创建类型")
    @PostMapping("/create")
    public R<Void> create(@RequestBody BookType bookType) {
        bookTypeService.createType(bookType);
        return R.success();
    }

    @ApiOperation("更新类型")
    @PostMapping("/update")
    public R<Void> update(@RequestBody BookType bookType) {
        bookTypeService.updateType(bookType);
        return R.success();
    }

    @ApiOperation("删除类型")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        bookTypeService.deleteType(id);
        return R.success();
    }
} 