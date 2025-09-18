package com.library.subscription.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.subscription.entity.Book;
import com.library.subscription.entity.BookType;
import com.library.subscription.entity.BookTypeRelation;
import com.library.subscription.mapper.BookMapper;
import com.library.subscription.mapper.BookTypeMapper;
import com.library.subscription.mapper.BookTypeRelationMapper;
import com.library.subscription.service.BookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务实现类
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookTypeRelationMapper bookTypeRelationMapper;

    @Autowired
    private BookTypeMapper bookTypeMapper;

    @Override
    public List<Book> getBookList(String keyword) {
        // 使用新的查询方法获取带分类信息的图书列表
        return bookMapper.getBookListWithTypes(keyword);
    }

    @Override
    public Book getBookDetail(String id) {
        try {
            // 使用XML映射的方法获取带有类型信息的图书
            Book book = bookMapper.getBookWithTypes(Long.valueOf(id));

            // 如果图书不存在，或者类型为空，则设置空列表
            if (book == null) {
                return null;
            }

            if (book.getBookTypes() == null) {
                book.setBookTypes(new ArrayList<>());
            }

            return book;
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常时返回基本信息
            return bookMapper.selectById(Long.valueOf(id));
        }
    }

    @Override
    public void createBook(Book book) {
        // 设置创建和更新时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        book.setCreateTime(now);
        book.setUpdateTime(now);

        // 设置默认值
        if (book.getBorrowCount() == null) {
            book.setBorrowCount(0);
        }
        if (StringUtils.isBlank(book.getStatus())) {
            book.setStatus("available");
        }

        // 保存图书基本信息
        bookMapper.insert(book);

        // 保存图书分类关系 - 优先使用bookTypeIds字段
        if (book.getBookTypeIds() != null && !book.getBookTypeIds().isEmpty()) {
            saveBookTypeRelationsFromIds(book.getId(), book.getBookTypeIds());
        }
        // 如果bookTypeIds为空，尝试使用bookTypes字段
        else if (book.getBookTypes() != null && !book.getBookTypes().isEmpty()) {
            saveBookTypeRelations(book.getId(), book.getBookTypes());
        }
    }

    @Override
    public void updateBook(Book book) {
        // 设置更新时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        book.setUpdateTime(now);

        // 更新图书基本信息
        bookMapper.updateById(book);

        // 如果传入了图书分类ID或分类对象，则更新分类关系
        if ((book.getBookTypeIds() != null && !book.getBookTypeIds().isEmpty()) ||
                (book.getBookTypes() != null && !book.getBookTypes().isEmpty())) {

            // 先删除原有关系
            LambdaQueryWrapper<BookTypeRelation> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(BookTypeRelation::getBookId, book.getId());
            bookTypeRelationMapper.delete(deleteWrapper);

            // 优先使用bookTypeIds保存新的关系
            if (book.getBookTypeIds() != null && !book.getBookTypeIds().isEmpty()) {
                saveBookTypeRelationsFromIds(book.getId(), book.getBookTypeIds());
            }
            // 如果bookTypeIds为空，尝试使用bookTypes
            else if (book.getBookTypes() != null && !book.getBookTypes().isEmpty()) {
                saveBookTypeRelations(book.getId(), book.getBookTypes());
            }
        }
    }

    @Override
    public void deleteBook(String id) {
        // 删除图书前，先删除关联的分类关系
        LambdaQueryWrapper<BookTypeRelation> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BookTypeRelation::getBookId, Long.valueOf(id));
        bookTypeRelationMapper.delete(deleteWrapper);

        // 删除图书
        bookMapper.deleteById(Long.valueOf(id));
    }

    @Override
    public List<Book> getBooksByType(String typeId) {
        // 使用新的查询方法获取带分类信息的图书列表
        return bookMapper.getBooksByTypeWithTypes(Long.valueOf(typeId));
    }

    /**
     * 保存图书分类关系
     * 
     * @param bookId    图书ID
     * @param bookTypes 图书分类列表
     */
    private void saveBookTypeRelations(Long bookId, List<BookType> bookTypes) {
        for (BookType type : bookTypes) {
            BookTypeRelation relation = new BookTypeRelation();
            relation.setBookId(bookId);
            relation.setTypeId(type.getId());
            bookTypeRelationMapper.insert(relation);
        }
    }

    /**
     * 保存图书分类关系 - 从ID列表
     * 
     * @param bookId  图书ID
     * @param typeIds 分类ID列表
     */
    private void saveBookTypeRelationsFromIds(Long bookId, List<Long> typeIds) {
        if (typeIds != null && !typeIds.isEmpty()) {
            for (Long typeId : typeIds) {
                BookTypeRelation relation = new BookTypeRelation();
                relation.setBookId(bookId);
                relation.setTypeId(typeId);
                bookTypeRelationMapper.insert(relation);
            }
        }
    }
}