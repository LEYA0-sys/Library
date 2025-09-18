package com.library.subscription.service;

import com.library.subscription.entity.Borrow;

import java.util.List;

/**
 * 借阅服务接口
 */
public interface BorrowService {
    /**
     * 获取借阅列表
     * 
     * @return 借阅列表
     */
    List<Borrow> getBorrowList();

    /**
     * 创建借阅记录
     * 
     * @param borrow 借阅对象
     */
    void createBorrow(Borrow borrow);

    /**
     * 删除借阅记录
     * 
     * @param id 借阅ID
     */
    void deleteBorrow(String id);

    /**
     * 更新借阅记录
     * 
     * @param borrow 借阅对象
     */
    void updateBorrow(Borrow borrow);

    /**
     * 根据用户ID和状态获取借阅列表
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 借阅列表
     */
    List<Borrow> getUserBorrowList(String userId, String status);

    /**
     * 获取借阅详情
     * 
     * @param id 借阅ID
     * @return 借阅对象
     */
    Borrow getBorrowDetail(Long id);

    /**
     * 借书
     * 
     * @param userId     用户ID
     * @param bookId     书籍ID
     * @param startDate  借阅开始日期（可选）
     * @param borrowDays 借阅天数（可选）
     * @return 借阅对象
     */
    Borrow borrowBook(String userId, String bookId, String startDate, Integer borrowDays);

    /**
     * 还书
     * 
     * @param id 借阅ID
     * @return 是否成功
     */
    boolean returnBook(String id);

    /**
     * 延长借阅时间
     * 
     * @param id   借阅ID
     * @param days 延长天数
     * @return 是否成功
     */
    boolean extendBorrowTime(String id, int days);
}