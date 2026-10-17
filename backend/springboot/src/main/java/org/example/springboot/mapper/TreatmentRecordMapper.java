package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.TreatmentRecord;

@Mapper
public interface TreatmentRecordMapper extends BaseMapper<TreatmentRecord> {
    /**
     * 根据就诊记录ID查询治疗记录列表（分页）
     */
    Page<TreatmentRecord> selectTreatmentRecordsByRecordId(Page<TreatmentRecord> page, @Param("recordId") Long recordId);
}