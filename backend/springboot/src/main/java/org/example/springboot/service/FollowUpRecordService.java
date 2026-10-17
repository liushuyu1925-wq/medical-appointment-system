package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Doctor;
import org.example.springboot.entity.FollowUpRecord;
import org.example.springboot.entity.MedicalRecord;
import org.example.springboot.entity.Patient;
import org.example.springboot.enumClass.StatusEnum;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.DoctorMapper;
import org.example.springboot.mapper.FollowUpRecordMapper;
import org.example.springboot.mapper.MedicalRecordMapper;
import org.example.springboot.mapper.PatientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowUpRecordService {
    @Resource
    private FollowUpRecordMapper followUpRecordMapper;
    
    @Resource
    private MedicalRecordMapper medicalRecordMapper;
    
    @Resource
    private PatientMapper patientMapper;
    
    @Resource
    private DoctorMapper doctorMapper;
    
    /**
     * 创建随访记录
     */
    @Transactional
    public FollowUpRecord createFollowUpRecord(FollowUpRecord followUpRecord) {
        // 检查就诊记录是否存在
        MedicalRecord medicalRecord = medicalRecordMapper.selectById(followUpRecord.getRecordId());
        if (medicalRecord == null) {
            throw new ServiceException("就诊记录不存在");
        }
        
        // 检查患者是否存在
        Patient patient = patientMapper.selectById(followUpRecord.getPatientId());
        if (patient == null) {
            throw new ServiceException("患者不存在");
        }
        
        // 检查医生是否存在
        Doctor doctor = doctorMapper.selectById(followUpRecord.getDoctorId());
        if (doctor == null) {
            throw new ServiceException("医生不存在");
        }
        
        // 如果没有指定随访日期，使用当前日期
        if (followUpRecord.getFollowUpDate() == null) {
            followUpRecord.setFollowUpDate(LocalDate.now());
        }
        
        // 设置状态为已完成
        followUpRecord.setStatus(StatusEnum.FINISHED.getCode());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        followUpRecord.setCreateTime(now);
        followUpRecord.setUpdateTime(now);
        
        if (followUpRecordMapper.insert(followUpRecord) <= 0) {
            throw new ServiceException("随访记录创建失败");
        }
        
        return followUpRecord;
    }
    
    /**
     * 更新随访记录
     */
    @Transactional
    public void updateFollowUpRecord(Long id, FollowUpRecord followUpRecord) {
        // 检查随访记录是否存在
        FollowUpRecord existingRecord = followUpRecordMapper.selectById(id);
        if (existingRecord == null) {
            throw new ServiceException("随访记录不存在");
        }
        
        followUpRecord.setId(id);
        followUpRecord.setUpdateTime(LocalDateTime.now());
        
        if (followUpRecordMapper.updateById(followUpRecord) <= 0) {
            throw new ServiceException("随访记录更新失败");
        }
    }
    
    /**
     * 获取随访记录详情
     */
    public FollowUpRecord getFollowUpRecordById(Long id) {
        FollowUpRecord followUpRecord = followUpRecordMapper.selectById(id);
        if (followUpRecord == null) {
            throw new ServiceException("随访记录不存在");
        }
        
        // 查询关联信息
        fillFollowUpRecordInfo(followUpRecord);
        
        return followUpRecord;
    }
    
    /**
     * 根据就诊记录ID查询随访记录列表
     */
    public Page<FollowUpRecord> getFollowUpRecordsByRecordId(Long recordId, Integer currentPage, Integer size) {
        Page<FollowUpRecord> page = new Page<>(currentPage, size);
        
        // 使用MyBatis-Plus的QueryWrapper构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FollowUpRecord> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("record_id", recordId);
        
        Page<FollowUpRecord> resultPage = followUpRecordMapper.selectPage(page, queryWrapper);
        
        // 填充关联信息
        for (FollowUpRecord record : resultPage.getRecords()) {
            fillFollowUpRecordInfo(record);
        }
        
        return resultPage;
    }
    
    /**
     * 根据患者ID查询随访记录列表
     */
    public Page<FollowUpRecord> getFollowUpRecordsByPatientId(Long patientId, Integer currentPage, Integer size) {
        Page<FollowUpRecord> page = new Page<>(currentPage, size);
        
        // 使用MyBatis-Plus的QueryWrapper构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FollowUpRecord> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("patient_id", patientId);
        
        Page<FollowUpRecord> resultPage = followUpRecordMapper.selectPage(page, queryWrapper);
        
        // 填充关联信息
        for (FollowUpRecord record : resultPage.getRecords()) {
            fillFollowUpRecordInfo(record);
        }
        
        return resultPage;
    }
    
    /**
     * 根据医生ID查询随访记录列表
     */
    public Page<FollowUpRecord> getFollowUpRecordsByDoctorId(Long doctorId, Integer currentPage, Integer size) {
        Page<FollowUpRecord> page = new Page<>(currentPage, size);
        
        // 使用MyBatis-Plus的QueryWrapper构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FollowUpRecord> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("doctor_id", doctorId);
        
        Page<FollowUpRecord> resultPage = followUpRecordMapper.selectPage(page, queryWrapper);
        
        // 填充关联信息
        for (FollowUpRecord record : resultPage.getRecords()) {
            fillFollowUpRecordInfo(record);
        }
        
        return resultPage;
    }
    
    /**
     * 删除随访记录
     */
    @Transactional
    public void deleteFollowUpRecord(Long id) {
        // 检查随访记录是否存在
        FollowUpRecord followUpRecord = followUpRecordMapper.selectById(id);
        if (followUpRecord == null) {
            throw new ServiceException("随访记录不存在");
        }
        
        if (followUpRecordMapper.deleteById(id) <= 0) {
            throw new ServiceException("随访记录删除失败");
        }
    }
    
    /**
     * 获取所有随访记录列表
     */
    public Page<FollowUpRecord> getAllFollowUpRecords(Integer currentPage, Integer size) {
        Page<FollowUpRecord> page = new Page<>(currentPage, size);
        
        // 使用MyBatis-Plus的QueryWrapper构建查询条件（空条件表示查询所有）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FollowUpRecord> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        Page<FollowUpRecord> resultPage = followUpRecordMapper.selectPage(page, queryWrapper);
        
        // 填充关联信息
        for (FollowUpRecord record : resultPage.getRecords()) {
            fillFollowUpRecordInfo(record);
        }
        
        return resultPage;
    }
    
    /**
     * 填充随访记录关联信息
     */
    private void fillFollowUpRecordInfo(FollowUpRecord followUpRecord) {
        // 查询患者信息
        if (followUpRecord.getPatientId() != null) {
            Patient patient = patientMapper.selectById(followUpRecord.getPatientId());
            followUpRecord.setPatient(patient);
        }
        
        // 查询医生信息
        if (followUpRecord.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(followUpRecord.getDoctorId());
            followUpRecord.setDoctor(doctor);
        }
    }
}