package com.library.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.subscription.entity.CreditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 信用记录Mapper接口
 */
@Mapper
public interface CreditRecordMapper extends BaseMapper<CreditRecord> {
    /**
     * 获取带有用户信息的信用记录列表
     * 
     * @return 信用记录列表
     */
    List<CreditRecord> getCreditRecordWithUser();

    /**
     * 获取用户的信用记录列表
     * 
     * @param userId 用户ID
     * @return 信用记录列表
     */
    List<CreditRecord> getUserCreditRecords(@Param("userId") Long userId);
}