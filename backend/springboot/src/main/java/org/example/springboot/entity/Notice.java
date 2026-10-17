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
@TableName("notice")
@Schema(description = "站内通知实体")
public class Notice {
    @TableId(type = IdType.AUTO)
    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "接收用户ID")
    private Long recipientUserId;

    @Schema(description = "接收角色编码")
    private String recipientRole;

    @Schema(description = "预约ID")
    private Long appointmentId;

    @Schema(description = "医生ID")
    private Long doctorId;

    @Schema(description = "科室ID")
    private Long departmentId;

    @Schema(description = "通知类型")
    private String noticeType;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "已读状态(0:未读,1:已读)")
    private Integer readStatus;

    @Schema(description = "发送状态(0:失败,1:成功)")
    private Integer sendStatus;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;

    @Schema(description = "已读时间")
    private LocalDateTime readTime;

    @Schema(description = "状态(0:停用,1:有效)")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @Schema(description = "接收人姓名")
    private String recipientName;

    @TableField(exist = false)
    @Schema(description = "接收人用户名")
    private String recipientUsername;

    @TableField(exist = false)
    @Schema(description = "医生姓名")
    private String doctorName;

    @TableField(exist = false)
    @Schema(description = "科室名称")
    private String departmentName;

    @TableField(exist = false)
    @Schema(description = "预约编号")
    private String appointmentNo;

    @TableField(exist = false)
    @Schema(description = "预约日期")
    private LocalDate appointmentDate;

    @TableField(exist = false)
    @Schema(description = "时间段")
    private String timeSlot;
}
