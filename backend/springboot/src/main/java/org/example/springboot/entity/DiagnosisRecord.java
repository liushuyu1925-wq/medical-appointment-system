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
@TableName("diagnosis_record")
@Schema(description = "诊断记录实体类")
public class DiagnosisRecord {
    @TableId(type = IdType.AUTO)
    @Schema(description = "诊断记录ID")
    private Long id;
    
    @Schema(description = "关联就诊记录ID")
    private Long recordId;
    
    @Schema(description = "诊断代码")
    private String diagnosisCode;
    
    @Schema(description = "诊断名称")
    private String diagnosisName;
    
    @Schema(description = "严重程度")
    private String severity;
    
    @Schema(description = "诊断日期")
    private LocalDate diagnosisDate;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    @Schema(description = "就诊记录信息")
    private MedicalRecord medicalRecord;
}