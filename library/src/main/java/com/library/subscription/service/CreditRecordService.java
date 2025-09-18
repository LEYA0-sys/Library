package com.library.subscription.service;

import com.library.subscription.entity.CreditRecord;
import java.util.List;

public interface CreditRecordService {
    List<CreditRecord> getCreditRecordList();

    List<CreditRecord> getUserCreditRecords(String userId);

    void createCreditRecord(CreditRecord creditRecord);

    void deleteCreditRecord(String id);

    boolean updateUserCredit(String userId, int change, String reason);
}