package com.library.subscription.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.subscription.entity.Book;
import com.library.subscription.entity.Borrow;
import com.library.subscription.entity.User;
import com.library.subscription.mapper.BookMapper;
import com.library.subscription.mapper.BorrowMapper;
import com.library.subscription.mapper.UserMapper;
import com.library.subscription.service.BorrowService;
import com.library.subscription.service.CreditRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 借阅服务实现类
 */
@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CreditRecordService creditRecordService;

    @Override
    public List<Borrow> getBorrowList() {
        // 使用mapper的SQL方法获取带图书名称的借阅列表
        return borrowMapper.getBorrowList(null);
    }

    @Override
    public void createBorrow(Borrow borrow) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        borrow.setCreateTime(now);
        borrow.setUpdateTime(now);
        borrowMapper.insert(borrow);
    }

    @Override
    public void deleteBorrow(String id) {
        borrowMapper.deleteById(Long.valueOf(id));
    }

    @Override
    public void updateBorrow(Borrow borrow) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        borrow.setUpdateTime(now);
        borrowMapper.updateById(borrow);
    }

    @Override
    public List<Borrow> getUserBorrowList(String userId, String status) {
        // 使用mapper的SQL方法获取带图书名称的借阅记录
        return borrowMapper.getUserBorrowList(Long.valueOf(userId), status);
    }

    @Override
    public Borrow getBorrowDetail(Long id) {
        // 使用mapper的SQL方法获取借阅详情
        return borrowMapper.getBorrowDetail(id);
    }

    @Override
    @Transactional
    public Borrow borrowBook(String userId, String bookId, String startDate, Integer borrowDays) {
        // 创建借阅记录
        Borrow borrow = new Borrow();
        borrow.setUserId(Long.valueOf(userId));
        borrow.setBookId(Long.valueOf(bookId));

        // 处理借阅时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        borrow.setCreateTime(now);
        borrow.setUpdateTime(now);

        // 设置借阅开始时间
        LocalDateTime startDateTime;
        if (startDate != null && !startDate.isEmpty()) {
            // 使用用户指定的开始日期
            startDateTime = LocalDate.parse(startDate).atStartOfDay();
        } else {
            // 使用当前时间作为开始日期
            startDateTime = LocalDateTime.now();
        }
        borrow.setStartTime(startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 设置预计归还时间（使用指定的借阅天数，默认30天）
        int days = borrowDays != null && borrowDays > 0 ? borrowDays : 30;
        String expectTime = startDateTime.plusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        borrow.setExpectTime(expectTime);

        // 设置状态为借阅中
        borrow.setBorrowStatus("borrowing");
        borrow.setHasDelayed("0"); // 未延期

        // 更新用户借阅次数
        User user = userMapper.selectById(Long.valueOf(userId));
        if (user != null) {
            user.setReadingCount(user.getReadingCount() + 1);
            userMapper.updateById(user);
        }

        // 更新图书状态和借阅次数
        Book book = bookMapper.selectById(Long.valueOf(bookId));
        if (book != null) {
            // 将图书状态改为不可借阅
            book.setStatus("unavailable");
            // 增加借阅次数
            book.setBorrowCount(book.getBorrowCount() + 1);
            // 更新图书信息
            bookMapper.updateById(book);
        }

        borrowMapper.insert(borrow);
        return borrow;
    }

    @Override
    @Transactional
    public boolean returnBook(String id) {
        Borrow borrow = borrowMapper.selectById(Long.valueOf(id));
        if (borrow != null
                && ("borrowing".equals(borrow.getBorrowStatus()) || "overdue".equals(borrow.getBorrowStatus()))) {
            // 获取图书信息
            Book book = bookMapper.selectById(borrow.getBookId());
            String bookName = book != null ? book.getBookName() : "未知图书";

            // 设置实际归还时间
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            borrow.setEndTime(now);
            borrow.setUpdateTime(now);

            // 判断是否逾期
            boolean isOverdue = "overdue".equals(borrow.getBorrowStatus());
            LocalDateTime expectTime = LocalDateTime.parse(borrow.getExpectTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime currentTime = LocalDateTime.now();

            // 如果之前没有标记为逾期，但实际已经逾期
            if (!isOverdue && currentTime.isAfter(expectTime)) {
                isOverdue = true;
            }

            // 设置状态为已归还
            borrow.setBorrowStatus("returned");

            // 更新图书状态为可借阅
            if (book != null) {
                book.setStatus("available");
                bookMapper.updateById(book);
            }

            // 更新用户在读书数减1
            User user = userMapper.selectById(borrow.getUserId());
            if (user != null && user.getReadingCount() > 0) {
                user.setReadingCount(user.getReadingCount() - 1);
                userMapper.updateById(user);

                // 根据是否逾期调整信用积分
                if (isOverdue) {
                    // 逾期还书，扣除信用积分
                    creditRecordService.updateUserCredit(
                            user.getId().toString(),
                            -10,
                            "逾期归还图书《" + bookName + "》");
                } else {
                    // 按时还书，增加信用积分
                    creditRecordService.updateUserCredit(
                            user.getId().toString(),
                            +2,
                            "按时归还图书《" + bookName + "》");
                }
            }

            borrowMapper.updateById(borrow);
            return true;
        }
        return false;
    }

    @Override
    public boolean extendBorrowTime(String id, int days) {
        Borrow borrow = borrowMapper.selectById(Long.valueOf(id));
        if (borrow != null && "borrowing".equals(borrow.getBorrowStatus())) {
            // 计算新的预计归还时间
            LocalDateTime expectTime = LocalDateTime.parse(borrow.getExpectTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String newExpectTime = expectTime.plusDays(days)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            borrow.setExpectTime(newExpectTime);
            borrow.setHasDelayed("1"); // 已延期
            borrow.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            borrowMapper.updateById(borrow);
            return true;
        }
        return false;
    }
}