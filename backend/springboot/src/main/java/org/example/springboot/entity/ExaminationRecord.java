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
@TableName("examination_record")
@Schema(description = "检查记录实体类")
public class ExaminationRecord {
    @TableId(type = IdType.AUTO)
    @Schema(description = "检查记录ID")
    private Long id;
    
    @Schema(description = "关联就诊记录ID")
    private Long recordId;
    
    @Schema(description = "检查类型")
    private String examinationType;
    
    @Schema(description = "检查项目名称")
    private String examinationName;
    
    @Schema(description = "检查结果")
    private String examinationResult;
    
    @Schema(description = "正常范围")
    private String normalRange;
    
    @Schema(description = "单位")
    private String unit;
    
    @Schema(description = "检查日期")
    private LocalDate examinationDate;
    
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