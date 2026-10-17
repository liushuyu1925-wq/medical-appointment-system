package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.DTO.StatisticsDTO;
import org.example.springboot.DTO.TrendDTO;
import org.example.springboot.DTO.DepartmentDistributionDTO;
import org.example.springboot.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "统计分析接口")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Resource
    private StatisticsService statisticsService;

    @Operation(summary = "获取统计概览")
    @GetMapping("/overview")
    public Result<?> getOverviewStatistics() {
        StatisticsDTO statistics = statisticsService.getOverviewStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "获取患者就诊趋势")
    @GetMapping("/patient-trend")
    public Result<?> getPatientTrend(@RequestParam(defaultValue = "6") int months) {
        List<TrendDTO> trends = statisticsService.getPatientTrend(months);
        return Result.success(trends);
    }

    @Operation(summary = "获取科室就诊分布")
    @GetMapping("/department-distribution")
    public Result<?> getDepartmentDistribution() {
        List<DepartmentDistributionDTO> distributions = statisticsService.getDepartmentDistribution();
        return Result.success(distributions);
    }

    @Operation(summary = "获取预约趋势")
    @GetMapping("/appointment-trend")
    public Result<?> getAppointmentTrend(@RequestParam(defaultValue = "6") int months) {
        List<TrendDTO> trends = statisticsService.getAppointmentTrend(months);
        return Result.success(trends);
    }

    @Operation(summary = "获取报告类型分布")
    @GetMapping("/report-type-distribution")
    public Result<?> getReportTypeDistribution() {
        List<TrendDTO> distributions = statisticsService.getReportTypeDistribution();
        return Result.success(distributions);
    }

    @Operation(summary = "获取随访状态分布")
    @GetMapping("/follow-up-status-distribution")
    public Result<?> getFollowUpStatusDistribution() {
        List<TrendDTO> distributions = statisticsService.getFollowUpStatusDistribution();
        return Result.success(distributions);
    }
}
