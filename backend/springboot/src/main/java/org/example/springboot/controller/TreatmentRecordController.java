package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.TreatmentRecord;
import org.example.springboot.service.TreatmentRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "治疗记录管理接口")
@RestController
@RequestMapping("/treatment")
public class TreatmentRecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreatmentRecordController.class);
    
    @Resource
    private TreatmentRecordService treatmentRecordService;

    @Operation(summary = "新增治疗记录")
    @PostMapping
    public Result<?> createTreatmentRecord(@RequestBody TreatmentRecord treatmentRecord) {
        LOGGER.info("新增治疗记录: recordId={}", treatmentRecord.getRecordId());
        TreatmentRecord newRecord = treatmentRecordService.createTreatmentRecord(treatmentRecord);
        return Result.success(newRecord);
    }
    
    @Operation(summary = "更新治疗记录")
    @PutMapping("/{id}")
    public Result<?> updateTreatmentRecord(@PathVariable Long id, @RequestBody TreatmentRecord treatmentRecord) {
        LOGGER.info("更新治疗记录: id={}", id);
        treatmentRecordService.updateTreatmentRecord(id, treatmentRecord);
        return Result.success();
    }
    
    @Operation(summary = "获取治疗记录详情")
    @GetMapping("/{id}")
    public Result<?> getTreatmentRecordById(@PathVariable Long id) {
        TreatmentRecord treatmentRecord = treatmentRecordService.getTreatmentRecordById(id);
        return Result.success(treatmentRecord);
    }
    
    @Operation(summary = "根据就诊记录ID查询治疗记录列表")
    @GetMapping("/record/{recordId}")
    public Result<?> getTreatmentRecordsByRecordId(
            @PathVariable Long recordId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<TreatmentRecord> page = treatmentRecordService.getTreatmentRecordsByRecordId(recordId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "删除治疗记录")
    @DeleteMapping("/{id}")
    public Result<?> deleteTreatmentRecord(@PathVariable Long id) {
        LOGGER.info("删除治疗记录: id={}", id);
        treatmentRecordService.deleteTreatmentRecord(id);
        return Result.success();
    }
}