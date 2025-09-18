package com.library.subscription.controller;

import com.library.subscription.common.R;
import com.library.subscription.entity.CreditRecord;
import com.library.subscription.service.CreditRecordService;
import com.library.subscription.task.CreditCheckTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Api(tags = "Credit Record Management")
@RestController
@RequestMapping("/credit")
public class CreditRecordController {

    @Autowired
    private CreditRecordService creditRecordService;

    @Autowired
    private CreditCheckTask creditCheckTask;

    @ApiOperation("Get Credit Record List")
    @GetMapping("/list")
    public R<List<CreditRecord>> list() {
        List<CreditRecord> list = creditRecordService.getCreditRecordList();
        return R.success(list);
    }

    @ApiOperation("Get User Credit Records")
    @GetMapping("/user/{userId}")
    public R<List<CreditRecord>> getUserRecords(@PathVariable String userId) {
        List<CreditRecord> list = creditRecordService.getUserCreditRecords(userId);
        return R.success(list);
    }

    @ApiOperation("Create Credit Record")
    @PostMapping("/create")
    public R<Void> create(@RequestBody CreditRecord creditRecord) {
        creditRecordService.createCreditRecord(creditRecord);
        return R.success();
    }

    @ApiOperation("Delete Credit Record")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        creditRecordService.deleteCreditRecord(id);
        return R.success();
    }

    @ApiOperation("Update User Credit")
    @PostMapping("/update-credit")
    public R<Void> updateCredit(@RequestParam String userId,
            @RequestParam Integer change,
            @RequestParam String reason) {
        boolean result = creditRecordService.updateUserCredit(userId, change, reason);
        if (result) {
            return R.success();
        } else {
            return R.error("Update credit failed");
        }
    }

    @ApiOperation("Manually check overdue books and update credit scores")
    @PostMapping("/check-overdue")
    public R<Map<String, Object>> checkOverdueBooks() {
        try {
            creditCheckTask.checkOverdueBooks();

            Map<String, Object> result = new HashMap<>();
            result.put("message", "逾期检查完成，已自动扣除相应信用分");
            return R.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("检查逾期图书失败: " + e.getMessage());
        }
    }
}