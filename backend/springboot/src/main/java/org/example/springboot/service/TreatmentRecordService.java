package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.MedicalRecord;
import org.example.springboot.entity.TreatmentRecord;
import org.example.springboot.enumClass.StatusEnum;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.MedicalRecordMapper;
import org.example.springboot.mapper.TreatmentRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TreatmentRecordService {
    @Resource
    private TreatmentRecordMapper treatmentRecordMapper;
    
    @Resource
    private MedicalRecordMapper medicalRecordMapper;
    
    /**
     * 创建治疗记录
     */
    @Transactional
    public TreatmentRecord createTreatmentRecord(TreatmentRecord treatmentRecord) {
        // 检查就诊记录是否存在
        MedicalRecord medicalRecord = medicalRecordMapper.selectById(treatmentRecord.getRecordId());
        if (medicalRecord == null) {
            throw new ServiceException("就诊记录不存在");
        }
        
        // 设置状态为进行中
        treatmentRecord.setStatus(StatusEnum.IN_PROGRESS.getCode());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        treatmentRecord.setCreateTime(now);
        treatmentRecord.setUpdateTime(now);
        
        if (treatmentRecordMapper.insert(treatmentRecord) <= 0) {
            throw new ServiceException("治疗记录创建失败");
        }
        
        return treatmentRecord;
    }
    
    /**
     * 更新治疗记录
     */
    @Transactional
    public void updateTreatmentRecord(Long id, TreatmentRecord treatmentRecord) {
        // 检查治疗记录是否存在
        TreatmentRecord existingRecord = treatmentRecordMapper.selectById(id);
        if (existingRecord == null) {
            throw new ServiceException("治疗记录不存在");
        }
        
        treatmentRecord.setId(id);
        treatmentRecord.setUpdateTime(LocalDateTime.now());
        
        if (treatmentRecordMapper.updateById(treatmentRecord) <= 0) {
            throw new ServiceException("治疗记录更新失败");
        }
    }
    
    /**
     * 获取治疗记录详情
     */
    public TreatmentRecord getTreatmentRecordById(Long id) {
        TreatmentRecord treatmentRecord = treatmentRecordMapper.selectById(id);
        if (treatmentRecord == null) {
            throw new ServiceException("治疗记录不存在");
        }
        
        // 查询关联信息
        fillTreatmentRecordInfo(treatmentRecord);
        
        return treatmentRecord;
    }
    
    /**
     * 根据就诊记录ID查询治疗记录列表
     */
    public Page<TreatmentRecord> getTreatmentRecordsByRecordId(Long recordId, Integer currentPage, Integer size) {
        Page<TreatmentRecord> page = new Page<>(currentPage, size);
        Page<TreatmentRecord> resultPage = treatmentRecordMapper.selectTreatmentRecordsByRecordId(page, recordId);
        
        // 填充关联信息
        for (TreatmentRecord record : resultPage.getRecords()) {
            fillTreatmentRecordInfo(record);
        }
        
        return resultPage;
    }
    
    /**
     * 删除治疗记录
     */
    @Transactional
    public void deleteTreatmentRecord(Long id) {
        // 检查治疗记录是否存在
        TreatmentRecord treatmentRecord = treatmentRecordMapper.selectById(id);
        if (treatmentRecord == null) {
            throw new ServiceException("治疗记录不存在");
        }
        
        if (treatmentRecordMapper.deleteById(id) <= 0) {
            throw new ServiceException("治疗记录删除失败");
        }
    }
    
    /**
     * 填充治疗记录关联信息
     */
    private void fillTreatmentRecordInfo(TreatmentRecord treatmentRecord) {
        // 查询就诊记录信息
        if (treatmentRecord.getRecordId() != null) {
            MedicalRecord medicalRecord = medicalRecordMapper.selectById(treatmentRecord.getRecordId());
            treatmentRecord.setMedicalRecord(medicalRecord);
        }
    }
}