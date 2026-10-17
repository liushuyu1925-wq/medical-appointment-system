package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.FollowUpRecord;
import java.util.List;

@Mapper
public interface FollowUpRecordMapper extends BaseMapper<FollowUpRecord> {
    /**
     * 根据就诊记录ID查询随访记录列表（分页）
     */
    Page<FollowUpRecord> selectFollowUpRecordsByRecordId(Page<FollowUpRecord> page, @Param("recordId") Long recordId);
    
    /**
     * 根据患者ID查询随访记录列表（分页）
     */
    Page<FollowUpRecord> selectFollowUpRecordsByPatientId(Page<FollowUpRecord> page, @Param("patientId") Long patientId);
    
    /**
     * 根据医生ID查询随访记录列表（分页）
     */
    Page<FollowUpRecord> selectFollowUpRecordsByDoctorId(Page<FollowUpRecord> page, @Param("doctorId") Long doctorId);
    
    /**
     * 获取随访状态分布
     */
    List<java.util.Map<String, Object>> selectStatusDistribution();
}