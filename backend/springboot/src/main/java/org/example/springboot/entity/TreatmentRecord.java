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
@TableName("treatment_record")
@Schema(description = "治疗记录实体类")
public class TreatmentRecord {
    @TableId(type = IdType.AUTO)
    @Schema(description = "治疗记录ID")
    private Long id;
    
    @Schema(description = "关联就诊记录ID")
    private Long recordId;
    
    @Schema(description = "治疗类型")
    private String treatmentType;
    
    @Schema(description = "治疗项目名称")
    private String treatmentName;
    
    @Schema(description = "剂量")
    private String dosage;
    
    @Schema(description = "频率")
    private String frequency;
    
    @Schema(description = "疗程")
    private String duration;
    
    @Schema(description = "开始日期")
    private LocalDate startDate;
    
    @Schema(description = "结束日期")
    private LocalDate endDate;
    
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