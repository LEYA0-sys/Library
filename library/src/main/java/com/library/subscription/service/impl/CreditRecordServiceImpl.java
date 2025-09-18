package com.library.subscription.service.impl;

import com.library.subscription.entity.CreditRecord;
import com.library.subscription.entity.User;
import com.library.subscription.mapper.CreditRecordMapper;
import com.library.subscription.mapper.UserMapper;
import com.library.subscription.service.CreditRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CreditRecordServiceImpl implements CreditRecordService {

    @Autowired
    private CreditRecordMapper creditRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<CreditRecord> getCreditRecordList() {
        return creditRecordMapper.getCreditRecordWithUser();
    }

    @Override
    public List<CreditRecord> getUserCreditRecords(String userId) {
        return creditRecordMapper.getUserCreditRecords(Long.valueOf(userId));
    }

    @Override
    public void createCreditRecord(CreditRecord creditRecord) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        creditRecord.setChangeTime(now);
        creditRecordMapper.insert(creditRecord);
    }

    @Override
    public void deleteCreditRecord(String id) {
        creditRecordMapper.deleteById(Long.valueOf(id));
    }

    @Override
    @Transactional
    public boolean updateUserCredit(String userId, int change, String reason) {
        try {
            User user = userMapper.selectById(Long.valueOf(userId));
            if (user == null) {
                return false;
            }

            int newCredit = user.getCredit() + change;
            newCredit = Math.max(newCredit, 0);
            user.setCredit(newCredit);

            userMapper.updateById(user);

            CreditRecord record = new CreditRecord();
            record.setUserId(Long.valueOf(userId));
            record.setCreditChange(change);
            record.setReason(reason);
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            record.setChangeTime(now);

            creditRecordMapper.insert(record);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}