package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.DiagnosisRecord;

@Mapper
public interface DiagnosisRecordMapper extends BaseMapper<DiagnosisRecord> {
    /**
     * 根据就诊记录ID查询诊断记录列表（分页）
     */
    Page<DiagnosisRecord> selectDiagnosisRecordsByRecordId(Page<DiagnosisRecord> page, @Param("recordId") Long recordId);
}