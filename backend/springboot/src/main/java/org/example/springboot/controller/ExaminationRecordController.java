package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.ExaminationRecord;
import org.example.springboot.service.ExaminationRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "检查记录管理接口")
@RestController
@RequestMapping("/examination")
public class ExaminationRecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaminationRecordController.class);
    
    @Resource
    private ExaminationRecordService examinationRecordService;

    @Operation(summary = "新增检查记录")
    @PostMapping
    public Result<?> createExaminationRecord(@RequestBody ExaminationRecord examinationRecord) {
        LOGGER.info("新增检查记录: recordId={}", examinationRecord.getRecordId());
        ExaminationRecord newRecord = examinationRecordService.createExaminationRecord(examinationRecord);
        return Result.success(newRecord);
    }
    
    @Operation(summary = "更新检查记录")
    @PutMapping("/{id}")
    public Result<?> updateExaminationRecord(@PathVariable Long id, @RequestBody ExaminationRecord examinationRecord) {
        LOGGER.info("更新检查记录: id={}", id);
        examinationRecordService.updateExaminationRecord(id, examinationRecord);
        return Result.success();
    }
    
    @Operation(summary = "获取检查记录详情")
    @GetMapping("/{id}")
    public Result<?> getExaminationRecordById(@PathVariable Long id) {
        ExaminationRecord examinationRecord = examinationRecordService.getExaminationRecordById(id);
        return Result.success(examinationRecord);
    }
    
    @Operation(summary = "根据就诊记录ID查询检查记录列表")
    @GetMapping("/record/{recordId}")
    public Result<?> getExaminationRecordsByRecordId(
            @PathVariable Long recordId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ExaminationRecord> page = examinationRecordService.getExaminationRecordsByRecordId(recordId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "删除检查记录")
    @DeleteMapping("/{id}")
    public Result<?> deleteExaminationRecord(@PathVariable Long id) {
        LOGGER.info("删除检查记录: id={}", id);
        examinationRecordService.deleteExaminationRecord(id);
        return Result.success();
    }
}