package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.FollowUpRecord;
import org.example.springboot.service.FollowUpRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "随访记录管理接口")
@RestController
@RequestMapping("/follow-up")
public class FollowUpRecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FollowUpRecordController.class);
    
    @Resource
    private FollowUpRecordService followUpRecordService;

    @Operation(summary = "新增随访记录")
    @PostMapping
    public Result<?> createFollowUpRecord(@RequestBody FollowUpRecord followUpRecord) {
        LOGGER.info("新增随访记录: recordId={}, patientId={}, doctorId={}", 
                followUpRecord.getRecordId(), followUpRecord.getPatientId(), followUpRecord.getDoctorId());
        FollowUpRecord newRecord = followUpRecordService.createFollowUpRecord(followUpRecord);
        return Result.success(newRecord);
    }
    
    @Operation(summary = "更新随访记录")
    @PutMapping("/{id}")
    public Result<?> updateFollowUpRecord(@PathVariable Long id, @RequestBody FollowUpRecord followUpRecord) {
        LOGGER.info("更新随访记录: id={}", id);
        followUpRecordService.updateFollowUpRecord(id, followUpRecord);
        return Result.success();
    }
    
    @Operation(summary = "获取随访记录详情")
    @GetMapping("/{id}")
    public Result<?> getFollowUpRecordById(@PathVariable Long id) {
        FollowUpRecord followUpRecord = followUpRecordService.getFollowUpRecordById(id);
        return Result.success(followUpRecord);
    }
    
    @Operation(summary = "根据就诊记录ID查询随访记录列表")
    @GetMapping("/record/{recordId}")
    public Result<?> getFollowUpRecordsByRecordId(
            @PathVariable Long recordId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<FollowUpRecord> page = followUpRecordService.getFollowUpRecordsByRecordId(recordId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "根据患者ID查询随访记录列表")
    @GetMapping("/patient/{patientId}")
    public Result<?> getFollowUpRecordsByPatientId(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<FollowUpRecord> page = followUpRecordService.getFollowUpRecordsByPatientId(patientId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "根据医生ID查询随访记录列表")
    @GetMapping("/doctor/{doctorId}")
    public Result<?> getFollowUpRecordsByDoctorId(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<FollowUpRecord> page = followUpRecordService.getFollowUpRecordsByDoctorId(doctorId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "删除随访记录")
    @DeleteMapping("/{id}")
    public Result<?> deleteFollowUpRecord(@PathVariable Long id) {
        LOGGER.info("删除随访记录: id={}", id);
        followUpRecordService.deleteFollowUpRecord(id);
        return Result.success();
    }
    
    @Operation(summary = "获取所有随访记录列表")
    @GetMapping("/page")
    public Result<?> getAllFollowUpRecords(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<FollowUpRecord> page = followUpRecordService.getAllFollowUpRecords(currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "更新随访记录状态")
    @PutMapping("/{id}/status")
    public Result<?> updateFollowUpStatus(@PathVariable Long id, @RequestBody FollowUpRecord followUpRecord) {
        LOGGER.info("更新随访记录状态: id={}, status={}", id, followUpRecord.getStatus());
        followUpRecordService.updateFollowUpRecord(id, followUpRecord);
        return Result.success();
    }
}