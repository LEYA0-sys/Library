import { defineStore } from 'pinia';
import { getBorrowList } from '@/api/borrow';

// 创建借阅状态管理
export const useBorrowStore = defineStore('borrow', {
  state: () => ({
    borrowedBooks: [], // 借阅中的图书ID列表
    initialized: false // 是否已初始化
  }),
  
  getters: {
    // 判断一本书是否已借阅
    isBookBorrowed: (state) => (bookId) => {
      return state.borrowedBooks.includes(Number(bookId)) || 
             state.borrowedBooks.includes(String(bookId));
    }
  },
  
  actions: {
    // 初始化借阅状态
    async initialize(userId) {
      if (this.initialized && this.borrowedBooks.length > 0) return;
      
      try {
        // 1. 先尝试从localStorage加载
        this.loadFromLocalStorage();
        
        // 2. 再从API获取最新数据，这才是最准确的
        const res = await getBorrowList();
        if (res.code === 200) {
          // 清空之前的状态，确保与后端同步
          this.borrowedBooks = [];
          
          // 提取当前用户借阅中的图书ID
          const borrowedIds = (res.data || [])
            .filter(item => 
              item.userId == userId && 
              (item.borrowStatus === 'borrowing' || 
               item.status === '借阅中')
            )
            .map(item => item.bookId);
          
          // 更新状态
          if (borrowedIds.length > 0) {
            this.borrowedBooks = [...new Set(borrowedIds)];
            // 保存到localStorage
            this.saveToLocalStorage();
          }
          
          this.initialized = true;
          console.log('借阅状态初始化完成，当前借阅图书IDs:', this.borrowedBooks);
        }
      } catch (error) {
        console.error('初始化借阅状态失败:', error);
      }
    },
    
    // 标记图书为已借阅
    borrowBook(bookId) {
      if (!this.isBookBorrowed(bookId)) {
        this.borrowedBooks.push(bookId);
        this.saveToLocalStorage();
      }
    },
    
    // 标记图书为已归还
    returnBook(bookId) {
      this.borrowedBooks = this.borrowedBooks.filter(id => 
        id !== bookId && Number(id) !== Number(bookId)
      );
      this.saveToLocalStorage();
    },
    
    // 从localStorage加载状态
    loadFromLocalStorage() {
      try {
        const storedData = localStorage.getItem('borrowedBooks');
        if (storedData) {
          this.borrowedBooks = JSON.parse(storedData);
        }
      } catch (e) {
        console.error('从localStorage加载借阅状态失败:', e);
      }
    },
    
    // 保存状态到localStorage
    saveToLocalStorage() {
      try {
        localStorage.setItem('borrowedBooks', JSON.stringify(this.borrowedBooks));
      } catch (e) {
        console.error('保存借阅状态到localStorage失败:', e);
      }
    },
    
    // 重置状态
    reset() {
      this.borrowedBooks = [];
      this.initialized = false;
      try {
        localStorage.removeItem('borrowedBooks');
      } catch (e) {
        console.error('重置借阅状态失败:', e);
      }
    }
  }
}); 