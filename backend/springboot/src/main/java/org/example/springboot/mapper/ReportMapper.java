package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.Report;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReportMapper extends BaseMapper<Report> {
    /**
     * 根据就诊记录ID查询报告列表（分页）
     */
    Page<Report> selectReportsByRecordId(Page<Report> page, @Param("recordId") Long recordId);
    
    /**
     * 根据患者ID查询报告列表（分页）
     */
    Page<Report> selectReportsByPatientId(Page<Report> page, @Param("patientId") Long patientId);
    
    /**
     * 根据医生ID查询报告列表（分页）
     */
    Page<Report> selectReportsByDoctorId(Page<Report> page, @Param("doctorId") Long doctorId);

    /**
     * 分页查询报告列表
     */
    @Select("SELECT * FROM report WHERE 1=1 ORDER BY create_time DESC")
    Page<Report> selectReportsByPage(Page<Report> page);
    
    /**
     * 获取报告类型分布
     */
    @Select("SELECT report_type as type, COUNT(*) as count FROM report GROUP BY report_type")
    List<Map<String, Object>> selectTypeDistribution();
}