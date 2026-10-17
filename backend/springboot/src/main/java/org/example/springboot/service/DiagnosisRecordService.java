package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.DiagnosisRecord;
import org.example.springboot.entity.MedicalRecord;
import org.example.springboot.enumClass.StatusEnum;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.DiagnosisRecordMapper;
import org.example.springboot.mapper.MedicalRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiagnosisRecordService {
    @Resource
    private DiagnosisRecordMapper diagnosisRecordMapper;
    
    @Resource
    private MedicalRecordMapper medicalRecordMapper;
    
    /**
     * 创建诊断记录
     */
    @Transactional
    public DiagnosisRecord createDiagnosisRecord(DiagnosisRecord diagnosisRecord) {
        // 检查就诊记录是否存在
        MedicalRecord medicalRecord = medicalRecordMapper.selectById(diagnosisRecord.getRecordId());
        if (medicalRecord == null) {
            throw new ServiceException("就诊记录不存在");
        }
        
        // 如果没有指定诊断日期，使用当前日期
        if (diagnosisRecord.getDiagnosisDate() == null) {
            diagnosisRecord.setDiagnosisDate(LocalDate.now());
        }
        
        // 设置状态为有效
        diagnosisRecord.setStatus(StatusEnum.VALID.getCode());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        diagnosisRecord.setCreateTime(now);
        diagnosisRecord.setUpdateTime(now);
        
        if (diagnosisRecordMapper.insert(diagnosisRecord) <= 0) {
            throw new ServiceException("诊断记录创建失败");
        }
        
        return diagnosisRecord;
    }
    
    /**
     * 更新诊断记录
     */
    @Transactional
    public void updateDiagnosisRecord(Long id, DiagnosisRecord diagnosisRecord) {
        // 检查诊断记录是否存在
        DiagnosisRecord existingRecord = diagnosisRecordMapper.selectById(id);
        if (existingRecord == null) {
            throw new ServiceException("诊断记录不存在");
        }
        
        diagnosisRecord.setId(id);
        diagnosisRecord.setUpdateTime(LocalDateTime.now());
        
        if (diagnosisRecordMapper.updateById(diagnosisRecord) <= 0) {
            throw new ServiceException("诊断记录更新失败");
        }
    }
    
    /**
     * 获取诊断记录详情
     */
    public DiagnosisRecord getDiagnosisRecordById(Long id) {
        DiagnosisRecord diagnosisRecord = diagnosisRecordMapper.selectById(id);
        if (diagnosisRecord == null) {
            throw new ServiceException("诊断记录不存在");
        }
        
        // 查询关联信息
        fillDiagnosisRecordInfo(diagnosisRecord);
        
        return diagnosisRecord;
    }
    
    /**
     * 根据就诊记录ID查询诊断记录列表
     */
    public Page<DiagnosisRecord> getDiagnosisRecordsByRecordId(Long recordId, Integer currentPage, Integer size) {
        Page<DiagnosisRecord> page = new Page<>(currentPage, size);
        Page<DiagnosisRecord> resultPage = diagnosisRecordMapper.selectDiagnosisRecordsByRecordId(page, recordId);
        
        // 填充关联信息
        for (DiagnosisRecord record : resultPage.getRecords()) {
            fillDiagnosisRecordInfo(record);
        }
        
        return resultPage;
    }
    
    /**
     * 删除诊断记录
     */
    @Transactional
    public void deleteDiagnosisRecord(Long id) {
        // 检查诊断记录是否存在
        DiagnosisRecord diagnosisRecord = diagnosisRecordMapper.selectById(id);
        if (diagnosisRecord == null) {
            throw new ServiceException("诊断记录不存在");
        }
        
        if (diagnosisRecordMapper.deleteById(id) <= 0) {
            throw new ServiceException("诊断记录删除失败");
        }
    }
    
    /**
     * 填充诊断记录关联信息
     */
    private void fillDiagnosisRecordInfo(DiagnosisRecord diagnosisRecord) {
        // 查询就诊记录信息
        if (diagnosisRecord.getRecordId() != null) {
            MedicalRecord medicalRecord = medicalRecordMapper.selectById(diagnosisRecord.getRecordId());
            diagnosisRecord.setMedicalRecord(medicalRecord);
        }
    }
}