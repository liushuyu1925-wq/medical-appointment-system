package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notice_log")
@Schema(description = "通知审计日志实体")
public class NoticeLog {
    @TableId(type = IdType.AUTO)
    @Schema(description = "通知日志ID")
    private Long id;

    @Schema(description = "通知ID")
    private Long noticeId;

    @Schema(description = "接收用户ID")
    private Long recipientUserId;

    @Schema(description = "操作类型")
    private String actionType;

    @Schema(description = "操作结果")
    private String actionResult;

    @Schema(description = "操作说明")
    private String actionMessage;

    @Schema(description = "操作人ID")
    private Long operatorUserId;

    @Schema(description = "操作人角色")
    private String operatorRole;

    @Schema(description = "任务批次号")
    private String taskBatchNo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(exist = false)
    @Schema(description = "通知标题")
    private String noticeTitle;

    @TableField(exist = false)
    @Schema(description = "接收人姓名")
    private String recipientName;

    @TableField(exist = false)
    @Schema(description = "操作人姓名")
    private String operatorName;
}
