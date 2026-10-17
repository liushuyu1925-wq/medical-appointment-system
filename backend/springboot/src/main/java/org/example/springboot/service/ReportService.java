package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Doctor;
import org.example.springboot.entity.MedicalRecord;
import org.example.springboot.entity.Patient;
import org.example.springboot.entity.Report;
import org.example.springboot.enumClass.StatusEnum;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.DoctorMapper;
import org.example.springboot.mapper.MedicalRecordMapper;
import org.example.springboot.mapper.PatientMapper;
import org.example.springboot.mapper.ReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Resource
    private ReportMapper reportMapper;
    
    @Resource
    private MedicalRecordMapper medicalRecordMapper;
    
    @Resource
    private PatientMapper patientMapper;
    
    @Resource
    private DoctorMapper doctorMapper;
    
    /**
     * 创建报告
     */
    @Transactional
    public Report createReport(Report report) {
        // 检查就诊记录是否存在
        MedicalRecord medicalRecord = medicalRecordMapper.selectById(report.getRecordId());
        if (medicalRecord == null) {
            throw new ServiceException("就诊记录不存在");
        }
        
        // 检查患者是否存在
        Patient patient = patientMapper.selectById(report.getPatientId());
        if (patient == null) {
            throw new ServiceException("患者不存在");
        }
        
        // 检查医生是否存在
        Doctor doctor = doctorMapper.selectById(report.getDoctorId());
        if (doctor == null) {
            throw new ServiceException("医生不存在");
        }
        
        // 生成报告编号
        report.setReportNo(generateReportNo());
        
        // 如果没有指定报告日期，使用当前日期
        if (report.getReportDate() == null) {
            report.setReportDate(LocalDate.now());
        }
        
        // 设置状态为正常
        report.setStatus(StatusEnum.NORMAL.getCode());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        report.setCreateTime(now);
        report.setUpdateTime(now);
        
        if (reportMapper.insert(report) <= 0) {
            throw new ServiceException("报告创建失败");
        }
        
        return report;
    }
    
    /**
     * 更新报告
     */
    @Transactional
    public void updateReport(Long id, Report report) {
        // 检查报告是否存在
        Report existingReport = reportMapper.selectById(id);
        if (existingReport == null) {
            throw new ServiceException("报告不存在");
        }
        
        report.setId(id);
        report.setUpdateTime(LocalDateTime.now());
        
        if (reportMapper.updateById(report) <= 0) {
            throw new ServiceException("报告更新失败");
        }
    }
    
    /**
     * 获取报告详情
     */
    public Report getReportById(Long id) {
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new ServiceException("报告不存在");
        }
        
        // 查询关联信息
        fillReportInfo(report);
        
        return report;
    }
    
    /**
     * 根据就诊记录ID查询报告列表
     */
    public Page<Report> getReportsByRecordId(Long recordId, Integer currentPage, Integer size) {
        Page<Report> page = new Page<>(currentPage, size);
        Page<Report> resultPage = reportMapper.selectReportsByRecordId(page, recordId);
        
        // 填充关联信息
        for (Report report : resultPage.getRecords()) {
            fillReportInfo(report);
        }
        
        return resultPage;
    }
    
    /**
     * 根据患者ID查询报告列表
     */
    public Page<Report> getReportsByPatientId(Long patientId, Integer currentPage, Integer size) {
        Page<Report> page = new Page<>(currentPage, size);
        Page<Report> resultPage = reportMapper.selectReportsByPatientId(page, patientId);
        
        // 填充关联信息
        for (Report report : resultPage.getRecords()) {
            fillReportInfo(report);
        }
        
        return resultPage;
    }
    
    /**
     * 根据医生ID查询报告列表
     */
    public Page<Report> getReportsByDoctorId(Long doctorId, Integer currentPage, Integer size) {
        Page<Report> page = new Page<>(currentPage, size);
        Page<Report> resultPage = reportMapper.selectReportsByDoctorId(page, doctorId);
        
        // 填充关联信息
        for (Report report : resultPage.getRecords()) {
            fillReportInfo(report);
        }
        
        return resultPage;
    }

    /**
     * 分页查询报告列表
     */
    public Page<Report> getReportsByPage(String reportNo, String reportType, String startDate, String endDate, Integer currentPage, Integer size) {
        Page<Report> page = new Page<>(currentPage, size);
        Page<Report> resultPage = reportMapper.selectReportsByPage(page);

        // 过滤结果
        List<Report> filteredReports = resultPage.getRecords().stream()
                .filter(report -> {
                    // 过滤报告编号
                    if (reportNo != null && !reportNo.isEmpty()) {
                        return report.getReportNo().contains(reportNo);
                    }
                    return true;
                })
                .filter(report -> {
                    // 过滤报告类型
                    if (reportType != null && !reportType.isEmpty()) {
                        return report.getReportType().equals(reportType);
                    }
                    return true;
                })
                .filter(report -> {
                    // 过滤开始日期
                    if (startDate != null && !startDate.isEmpty()) {
                        return report.getReportDate() != null && !report.getReportDate().isBefore(LocalDate.parse(startDate));
                    }
                    return true;
                })
                .filter(report -> {
                    // 过滤结束日期
                    if (endDate != null && !endDate.isEmpty()) {
                        return report.getReportDate() != null && !report.getReportDate().isAfter(LocalDate.parse(endDate));
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 填充关联信息
        for (Report report : filteredReports) {
            fillReportInfo(report);
        }

        // 创建新的分页结果
        Page<Report> filteredPage = new Page<>(currentPage, size);
        filteredPage.setRecords(filteredReports);
        filteredPage.setTotal(filteredReports.size());

        return filteredPage;
    }
    
    /**
     * 删除报告
     */
    @Transactional
    public void deleteReport(Long id) {
        // 检查报告是否存在
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new ServiceException("报告不存在");
        }
        
        if (reportMapper.deleteById(id) <= 0) {
            throw new ServiceException("报告删除失败");
        }
    }
    
    /**
     * 生成报告编号
     */
    private String generateReportNo() {
        // 生成格式：REPORT + 年月日 + 6位随机数
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", (int)(Math.random() * 1000000));
        return "REPORT" + dateStr + randomStr;
    }
    
    /**
     * 填充报告关联信息
     */
    private void fillReportInfo(Report report) {
        // 查询就诊记录信息
        if (report.getRecordId() != null) {
            MedicalRecord medicalRecord = medicalRecordMapper.selectById(report.getRecordId());
            report.setMedicalRecord(medicalRecord);
        }
        
        // 查询患者信息
        if (report.getPatientId() != null) {
            Patient patient = patientMapper.selectById(report.getPatientId());
            report.setPatient(patient);
        }
        
        // 查询医生信息
        if (report.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(report.getDoctorId());
            report.setDoctor(doctor);
        }
    }
}