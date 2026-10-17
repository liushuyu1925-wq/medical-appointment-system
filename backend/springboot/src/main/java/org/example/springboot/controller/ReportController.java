package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Report;
import org.example.springboot.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报告管理接口")
@RestController
@RequestMapping("/report")
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
    
    @Resource
    private ReportService reportService;

    @Operation(summary = "新增报告")
    @PostMapping
    public Result<?> createReport(@RequestBody Report report) {
        LOGGER.info("新增报告: recordId={}, patientId={}, doctorId={}", 
                report.getRecordId(), report.getPatientId(), report.getDoctorId());
        Report newReport = reportService.createReport(report);
        return Result.success(newReport);
    }
    
    @Operation(summary = "更新报告")
    @PutMapping("/{id}")
    public Result<?> updateReport(@PathVariable Long id, @RequestBody Report report) {
        LOGGER.info("更新报告: id={}", id);
        reportService.updateReport(id, report);
        return Result.success();
    }
    
    @Operation(summary = "获取报告详情")
    @GetMapping("/{id}")
    public Result<?> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return Result.success(report);
    }
    
    @Operation(summary = "根据就诊记录ID查询报告列表")
    @GetMapping("/record/{recordId}")
    public Result<?> getReportsByRecordId(
            @PathVariable Long recordId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Report> page = reportService.getReportsByRecordId(recordId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "根据患者ID查询报告列表")
    @GetMapping("/patient/{patientId}")
    public Result<?> getReportsByPatientId(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Report> page = reportService.getReportsByPatientId(patientId, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "根据医生ID查询报告列表")
    @GetMapping("/doctor/{doctorId}")
    public Result<?> getReportsByDoctorId(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Report> page = reportService.getReportsByDoctorId(doctorId, currentPage, size);
        return Result.success(page);
    }

    @Operation(summary = "分页查询报告列表")
    @GetMapping("/page")
    public Result<?> getReportsByPage(
            @RequestParam(required = false) String reportNo,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Report> page = reportService.getReportsByPage(reportNo, reportType, startDate, endDate, currentPage, size);
        return Result.success(page);
    }
    
    @Operation(summary = "删除报告")
    @DeleteMapping("/{id}")
    public Result<?> deleteReport(@PathVariable Long id) {
        LOGGER.info("删除报告: id={}", id);
        reportService.deleteReport(id);
        return Result.success();
    }
}