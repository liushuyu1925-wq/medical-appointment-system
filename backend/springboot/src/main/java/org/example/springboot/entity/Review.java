package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long appointmentId;
    
    private Long patientId;
    
    private Long doctorId;
    
    private Integer rating;
    
    private String content;
    
    private String reply;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}