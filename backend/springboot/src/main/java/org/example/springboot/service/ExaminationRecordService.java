package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.ExaminationRecord;
import org.example.springboot.entity.MedicalRecord;
import org.example.springboot.enumClass.StatusEnum;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.ExaminationRecordMapper;
import org.example.springboot.mapper.MedicalRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExaminationRecordService {
    @Resource
    private ExaminationRecordMapper examinationRecordMapper;
    
    @Resource
    private MedicalRecordMapper medicalRecordMapper;
    
    /**
     * 创建检查记录
     */
    @Transactional
    public ExaminationRecord createExaminationRecord(ExaminationRecord examinationRecord) {
        // 检查就诊记录是否存在
        MedicalRecord medicalRecord = medicalRecordMapper.selectById(examinationRecord.getRecordId());
        if (medicalRecord == null) {
            throw new ServiceException("就诊记录不存在");
        }
        
        // 如果没有指定检查日期，使用当前日期
        if (examinationRecord.getExaminationDate() == null) {
            examinationRecord.setExaminationDate(LocalDate.now());
        }
        
        // 设置状态为正常
        examinationRecord.setStatus(StatusEnum.NORMAL.getCode());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        examinationRecord.setCreateTime(now);
        examinationRecord.setUpdateTime(now);
        
        if (examinationRecordMapper.insert(examinationRecord) <= 0) {
            throw new ServiceException("检查记录创建失败");
        }
        
        return examinationRecord;
    }
    
    /**
     * 更新检查记录
     */
    @Transactional
    public void updateExaminationRecord(Long id, ExaminationRecord examinationRecord) {
        // 检查检查记录是否存在
        ExaminationRecord existingRecord = examinationRecordMapper.selectById(id);
        if (existingRecord == null) {
            throw new ServiceException("检查记录不存在");
        }
        
        examinationRecord.setId(id);
        examinationRecord.setUpdateTime(LocalDateTime.now());
        
        if (examinationRecordMapper.updateById(examinationRecord) <= 0) {
            throw new ServiceException("检查记录更新失败");
        }
    }
    
    /**
     * 获取检查记录详情
     */
    public ExaminationRecord getExaminationRecordById(Long id) {
        ExaminationRecord examinationRecord = examinationRecordMapper.selectById(id);
        if (examinationRecord == null) {
            throw new ServiceException("检查记录不存在");
        }
        
        // 查询关联信息
        fillExaminationRecordInfo(examinationRecord);
        
        return examinationRecord;
    }
    
    /**
     * 根据就诊记录ID查询检查记录列表
     */
    public Page<ExaminationRecord> getExaminationRecordsByRecordId(Long recordId, Integer currentPage, Integer size) {
        Page<ExaminationRecord> page = new Page<>(currentPage, size);
        Page<ExaminationRecord> resultPage = examinationRecordMapper.selectExaminationRecordsByRecordId(page, recordId);
        
        // 填充关联信息
        for (ExaminationRecord record : resultPage.getRecords()) {
            fillExaminationRecordInfo(record);
        }
        
        return resultPage;
    }
    
    /**
     * 删除检查记录
     */
    @Transactional
    public void deleteExaminationRecord(Long id) {
        // 检查检查记录是否存在
        ExaminationRecord examinationRecord = examinationRecordMapper.selectById(id);
        if (examinationRecord == null) {
            throw new ServiceException("检查记录不存在");
        }
        
        if (examinationRecordMapper.deleteById(id) <= 0) {
            throw new ServiceException("检查记录删除失败");
        }
    }
    
    /**
     * 填充检查记录关联信息
     */
    private void fillExaminationRecordInfo(ExaminationRecord examinationRecord) {
        // 查询就诊记录信息
        if (examinationRecord.getRecordId() != null) {
            MedicalRecord medicalRecord = medicalRecordMapper.selectById(examinationRecord.getRecordId());
            examinationRecord.setMedicalRecord(medicalRecord);
        }
    }
}