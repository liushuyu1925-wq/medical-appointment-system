package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.springboot.DTO.StatisticsDTO;
import org.example.springboot.DTO.TrendDTO;
import org.example.springboot.DTO.DepartmentDistributionDTO;
import org.example.springboot.entity.*;
import org.example.springboot.enumClass.FollowUpStatus;
import org.example.springboot.mapper.*;
import org.example.springboot.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private PatientMapper patientMapper;

    @Resource
    private DoctorMapper doctorMapper;

    @Resource
    private MedicalRecordMapper medicalRecordMapper;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private ReviewMapper reviewMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private FollowUpRecordMapper followUpRecordMapper;

    @Override
    public StatisticsDTO getOverviewStatistics() {
        StatisticsDTO dto = new StatisticsDTO();
        
        // 总患者数
        dto.setTotalPatients(patientMapper.selectCount(null));
        
        // 总医生数
        dto.setTotalDoctors(doctorMapper.selectCount(null));
        
        // 总就诊记录数
        dto.setTotalRecords(medicalRecordMapper.selectCount(null));
        
        // 总报告数
        dto.setTotalReports(reportMapper.selectCount(null));
        
        // 总预约数
        dto.setTotalAppointments(appointmentMapper.selectCount(null));
        
        // 总评价数
        dto.setTotalReviews(reviewMapper.selectCount(null));
        
        // 平均评分
        Double avgRating = reviewMapper.selectAvgRating();
        dto.setAvgRating(avgRating != null ? avgRating : 0.0);
        
        return dto;
    }

    @Override
    public List<TrendDTO> getPatientTrend(int months) {
        List<TrendDTO> trends = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            String monthLabel = targetDate.format(DateTimeFormatter.ofPattern("MM月"));
            
            LocalDate startOfMonth = targetDate.withDayOfMonth(1);
            LocalDate endOfMonth = targetDate.withDayOfMonth(targetDate.lengthOfMonth());
            
            long count = medicalRecordMapper.selectCount(
                new LambdaQueryWrapper<MedicalRecord>()
                    .ge(MedicalRecord::getRecordDate, startOfMonth)
                    .le(MedicalRecord::getRecordDate, endOfMonth)
            );
            
            TrendDTO trend = new TrendDTO();
            trend.setLabel(monthLabel);
            trend.setValue(count);
            trends.add(trend);
        }
        
        return trends;
    }

    @Override
    public List<DepartmentDistributionDTO> getDepartmentDistribution() {
        List<DepartmentDistributionDTO> distributions = new ArrayList<>();
        
        // 获取所有科室
        List<Department> departments = departmentMapper.selectList(null);
        Map<Long, String> departmentMap = departments.stream()
            .collect(Collectors.toMap(Department::getId, Department::getDeptName));
        
        // 获取每个科室的就诊人数
        List<Map<String, Object>> results = medicalRecordMapper.selectDepartmentDistribution();
        long totalCount = results.stream().mapToLong(r -> (Long) r.get("count")).sum();
        
        for (Map<String, Object> result : results) {
            Long departmentId = (Long) result.get("department_id");
            Long count = (Long) result.get("count");
            String departmentName = departmentMap.getOrDefault(departmentId, "未知科室");
            
            DepartmentDistributionDTO dto = new DepartmentDistributionDTO();
            dto.setDepartmentName(departmentName);
            dto.setCount(count);
            dto.setPercentage(totalCount > 0 ? (double) count / totalCount * 100 : 0);
            distributions.add(dto);
        }
        
        return distributions;
    }

    @Override
    public List<TrendDTO> getAppointmentTrend(int months) {
        List<TrendDTO> trends = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            String monthLabel = targetDate.format(DateTimeFormatter.ofPattern("MM月"));
            
            LocalDate startOfMonth = targetDate.withDayOfMonth(1);
            LocalDate endOfMonth = targetDate.withDayOfMonth(targetDate.lengthOfMonth());
            
            long count = appointmentMapper.selectCount(
                new LambdaQueryWrapper<Appointment>()
                    .ge(Appointment::getCreateTime, startOfMonth.atStartOfDay())
                    .le(Appointment::getCreateTime, endOfMonth.atTime(23, 59, 59))
            );
            
            TrendDTO trend = new TrendDTO();
            trend.setLabel(monthLabel);
            trend.setValue(count);
            trends.add(trend);
        }
        
        return trends;
    }

    @Override
    public List<TrendDTO> getReportTypeDistribution() {
        List<TrendDTO> distributions = new ArrayList<>();
        
        // 这里可以根据实际的报告类型字段进行查询
        // 假设报告表中有type字段
        List<Map<String, Object>> results = reportMapper.selectTypeDistribution();
        
        for (Map<String, Object> result : results) {
            String type = (String) result.get("type");
            Long count = (Long) result.get("count");
            
            TrendDTO dto = new TrendDTO();
            dto.setLabel(type != null ? type : "其他");
            dto.setValue(count);
            distributions.add(dto);
        }
        
        return distributions;
    }

    @Override
    public List<TrendDTO> getFollowUpStatusDistribution() {
        List<TrendDTO> distributions = new ArrayList<>();
        
        // 统计每种状态的数量
        long completedCount = followUpRecordMapper.selectCount(
            new LambdaQueryWrapper<FollowUpRecord>()
                .eq(FollowUpRecord::getStatus, FollowUpStatus.COMPLETED.getValue())
        );
        
        long inProgressCount = followUpRecordMapper.selectCount(
            new LambdaQueryWrapper<FollowUpRecord>()
                .eq(FollowUpRecord::getStatus, FollowUpStatus.IN_PROGRESS.getValue())
        );
        
        // 创建状态分布数据
        TrendDTO completed = new TrendDTO();
        completed.setLabel("已完成");
        completed.setValue(completedCount);
        distributions.add(completed);
        
        TrendDTO inProgress = new TrendDTO();
        inProgress.setLabel("进行中");
        inProgress.setValue(inProgressCount);
        distributions.add(inProgress);
        
        return distributions;
    }
}
