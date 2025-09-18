package com.library.subscription.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.subscription.entity.Borrow;
import com.library.subscription.mapper.BorrowMapper;
import com.library.subscription.service.CreditRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 信用积分检查任务
 * 定时检查逾期未还书并扣除信用积分
 */
@Component
public class CreditCheckTask {

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private CreditRecordService creditRecordService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void checkOverdueBooks() {
        String now = LocalDateTime.now().format(FORMATTER);

        // 查询所有借阅中且已经超过预期归还时间的记录
        LambdaQueryWrapper<Borrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Borrow::getBorrowStatus, "borrowing")
                .lt(Borrow::getExpectTime, now);

        List<Borrow> overdueBorrows = borrowMapper.selectList(queryWrapper);

        for (Borrow borrow : overdueBorrows) {
            // 更新借阅状态为逾期
            if (!"overdue".equals(borrow.getBorrowStatus())) {
                borrow.setBorrowStatus("overdue");

                // 扣除信用积分（每逾期一本书扣5分）
                creditRecordService.updateUserCredit(
                        borrow.getUserId().toString(),
                        -5, // 扣5分
                        "图书《" + getBorrowBookName(borrow) + "》逾期未归还");

                // 更新借阅记录
                borrowMapper.updateById(borrow);
            }
        }
    }

    /**
     * 获取借阅的图书名称
     */
    private String getBorrowBookName(Borrow borrow) {
        if (borrow.getBookName() != null) {
            return borrow.getBookName();
        }

        // 如果借阅记录中没有图书名称，可以通过图书ID查询
        // 为简化代码，这里直接返回图书ID
        return "ID:" + borrow.getBookId();
    }
}