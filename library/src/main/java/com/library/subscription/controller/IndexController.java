package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.service.BookService;
import com.library.subscription.service.BorrowService;
import com.library.subscription.service.UserService;
import com.library.subscription.entity.Book;
import com.library.subscription.entity.Borrow;
import com.library.subscription.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 */
@RestController
public class IndexController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private BorrowService borrowService;

    @GetMapping("/")
    public R<String> index() {
        return R.success("图书管理系统API接口");
    }

    @GetMapping("/stats")
    public R<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取图书总数
        List<Book> books = bookService.getBookList(null);
        stats.put("totalBooks", books != null ? books.size() : 0);

        // 获取用户总数
        List<User> users = userService.getUserList(null);
        stats.put("totalUsers", users != null ? users.size() : 0);

        // 获取借阅总数和当前借阅数
        List<Borrow> borrows = borrowService.getBorrowList();
        stats.put("totalBorrows", borrows != null ? borrows.size() : 0);

        // 计算当前借阅数（状态为borrowing的记录数）
        long activeBorrows = borrows != null ? borrows.stream()
                .filter(b -> "borrowing".equals(b.getBorrowStatus()))
                .count() : 0;
        stats.put("activeBorrows", activeBorrows);

        return R.success(stats);
    }
}