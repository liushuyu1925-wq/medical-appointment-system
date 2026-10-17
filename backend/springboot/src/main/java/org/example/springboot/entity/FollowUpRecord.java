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
@TableName("follow_up_record")
@Schema(description = "随访记录实体类")
public class FollowUpRecord {
    @TableId(type = IdType.AUTO)
    @Schema(description = "随访记录ID")
    private Long id;
    
    @Schema(description = "关联就诊记录ID")
    private Long recordId;
    
    @Schema(description = "患者ID")
    private Long patientId;
    
    @Schema(description = "医生ID")
    private Long doctorId;
    
    @Schema(description = "随访日期")
    private LocalDate followUpDate;
    
    @Schema(description = "随访方式")
    private String followUpType;
    
    @Schema(description = "随访内容")
    private String followUpContent;
    
    @Schema(description = "下次随访日期")
    private LocalDate nextFollowUp;
    
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