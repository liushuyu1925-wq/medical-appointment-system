package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.ExaminationRecord;

@Mapper
public interface ExaminationRecordMapper extends BaseMapper<ExaminationRecord> {
    /**
     * 根据就诊记录ID查询检查记录列表（分页）
     */
    Page<ExaminationRecord> selectExaminationRecordsByRecordId(Page<ExaminationRecord> page, @Param("recordId") Long recordId);
}