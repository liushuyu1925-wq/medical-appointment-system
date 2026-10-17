package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("report")
@Schema(description = "报告实体类")
public class Report {
    @TableId(type = IdType.AUTO)
    @Schema(description = "报告ID")
    private Long id;
    
    @Schema(description = "报告编号")
    private String reportNo;
    
    @Schema(description = "关联就诊记录ID")
    private Long recordId;
    
    @Schema(description = "患者ID")
    private Long patientId;
    
    @Schema(description = "医生ID")
    private Long doctorId;
    
    @Schema(description = "报告类型")
    private String reportType;
    
    @Schema(description = "报告名称")
    private String reportName;
    
    @Schema(description = "报告内容")
    private String reportContent;
    
    @Schema(description = "报告日期")
    private LocalDate reportDate;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    @Schema(description = "就诊记录信息")
    private MedicalRecord medicalRecord;
    
    @TableField(exist = false)
    @Schema(description = "患者信息")
    private Patient patient;
    
    @TableField(exist = false)
    @Schema(description = "医生信息")
    private Doctor doctor;
}