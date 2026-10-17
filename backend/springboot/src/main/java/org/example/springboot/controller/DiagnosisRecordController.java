package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.DiagnosisRecord;
import org.example.springboot.service.DiagnosisRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "诊断记录管理接口")
@RestController
@RequestMapping("/diagnosis")
public class DiagnosisRecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosisRecordController.class);
    
    @Resource
    private DiagnosisRecordService diagnosisRecordService;

    @Operation(summary = "新增诊断记录")
    @PostMapping
    public Result<?> createDiagnosisRecord(@RequestBody DiagnosisRecord diagnosisRecord) {
        LOGGER.info("新增诊断记录: recordId={}", diagnosisRecord.getRecordId());
        DiagnosisRecord newRecord = diagnosisRecordService.createDiagnosisRecord(diagnosisRecord);
        return Result.success(newRecord);
    }
    
    @Operation(summary = "更新诊断记录")
    @PutMapping("/{id}")
    public Result<?> updateDiagnosisRecord(@PathVariable Long id, @RequestBody DiagnosisRecord diagnosisRecord) {
        LOGGER.info("更新诊断记录: id={}", id);
        diagnosisRecordService.updateDiagnosisRecord(id, diagnosisRecord);
        return Result.success();
    }
    
    @Operation(summary = "获取诊断记录详情")
    @GetMapping("/{id}")
    public Result<?> getDiagnosisRecordById(@PathVariable Long id) {
        DiagnosisRecord diagnosisRecord = diagnosisRecordService.getDiagnosisRecordById(id);
        return Result.success(diagnosisRecord);
    }
    
    @Operation(summary = "根据就诊记录ID查询诊断记录列表")
    @GetMapping("/record/{recordId}")
    public Result<?> getDiagnosisRecordsByRecordId(
            @PathVariable Long recordId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<DiagnosisRecord> page = diagnosisRecordService.getDiagnosisRecordsByRecordId(recordId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "删除诊断记录")
    @DeleteMapping("/{id}")
    public Result<?> deleteDiagnosisRecord(@PathVariable Long id) {
        LOGGER.info("删除诊断记录: id={}", id);
        diagnosisRecordService.deleteDiagnosisRecord(id);
        return Result.success();
    }
}