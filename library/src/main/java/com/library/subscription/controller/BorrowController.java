package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.Borrow;
import com.library.subscription.service.BorrowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 借阅控制器
 */
@Api(tags = "借阅管理")
@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @ApiOperation("获取借阅列表")
    @GetMapping("/list")
    public R<List<Borrow>> list() {
        List<Borrow> list = borrowService.getBorrowList();
        return R.success(list);
    }

    @ApiOperation("获取借阅详情")
    @GetMapping("/detail/{id}")
    public R<Borrow> detail(@PathVariable Long id) {
        Borrow borrow = borrowService.getBorrowDetail(id);
        return R.success(borrow);
    }

    @ApiOperation("借书")
    @PostMapping("/borrow")
    public R<Borrow> borrow(
            @RequestParam String userId,
            @RequestParam String bookId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false, defaultValue = "30") Integer borrowDays) {
        Borrow borrow = borrowService.borrowBook(userId, bookId, startDate, borrowDays);
        return R.success(borrow);
    }

    @ApiOperation("还书")
    @PostMapping("/return/{id}")
    public R<Boolean> returnBook(@PathVariable String id) {
        boolean result = borrowService.returnBook(id);
        return R.success(result);
    }

    @ApiOperation("延长借阅时间")
    @PostMapping("/extend")
    public R<Boolean> extend(@RequestParam String id, @RequestParam Integer days) {
        boolean result = borrowService.extendBorrowTime(id, days);
        return R.success(result);
    }

    @ApiOperation("获取用户的借阅记录")
    @GetMapping("/user/{userId}")
    public R<List<Borrow>> getUserBorrows(@PathVariable String userId, @RequestParam(required = false) String status) {
        List<Borrow> list = borrowService.getUserBorrowList(userId, status);
        return R.success(list);
    }

    @ApiOperation("借阅统计")
    @GetMapping("/stats")
    public R<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Borrow> allBorrows = borrowService.getBorrowList();

        // 计算总借阅数
        stats.put("totalBorrows", allBorrows.size());

        // 计算在借数量
        long activeBorrows = allBorrows.stream()
                .filter(b -> "borrowing".equals(b.getBorrowStatus()))
                .count();
        stats.put("activeBorrows", activeBorrows);

        // 计算已归还数量
        long returnedBorrows = allBorrows.stream()
                .filter(b -> "returned".equals(b.getBorrowStatus()))
                .count();
        stats.put("returnedBorrows", returnedBorrows);

        return R.success(stats);
    }
}