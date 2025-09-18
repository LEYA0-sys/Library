package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.Book;
import com.library.subscription.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书控制器
 */
@Api(tags = "图书管理")
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @ApiOperation("获取图书列表")
    @GetMapping("/list")
    public R<List<Book>> list(@RequestParam(required = false) String keyword) {
        List<Book> list = bookService.getBookList(keyword);
        return R.success(list);
    }

    @ApiOperation("获取图书详情")
    @GetMapping("/detail/{id}")
    public R<Book> detail(@PathVariable String id) {
        Book book = bookService.getBookDetail(id);
        return R.success(book);
    }

    @ApiOperation("创建图书")
    @PostMapping("/create")
    public R<Void> create(@RequestBody Book book) {
        bookService.createBook(book);
        return R.success();
    }

    @ApiOperation("更新图书")
    @PostMapping("/update")
    public R<Void> update(@RequestBody Book book) {
        bookService.updateBook(book);
        return R.success();
    }

    @ApiOperation("删除图书")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        bookService.deleteBook(id);
        return R.success();
    }
    
    @ApiOperation("根据类型获取图书列表")
    @GetMapping("/type/{typeId}")
    public R<List<Book>> getBooksByType(@PathVariable String typeId) {
        List<Book> list = bookService.getBooksByType(typeId);
        return R.success(list);
    }
} 