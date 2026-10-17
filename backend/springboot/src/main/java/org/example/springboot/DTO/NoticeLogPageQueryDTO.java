package org.example.springboot.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通知日志分页查询参数")
public class NoticeLogPageQueryDTO {
    @Schema(description = "接收人关键字")
    private String recipientKeyword;

    @Schema(description = "操作类型")
    private String actionType;

    @Schema(description = "操作结果")
    private String actionResult;

    @Schema(description = "任务批次号")
    private String taskBatchNo;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "当前页")
    private Integer currentPage = 1;

    @Schema(description = "每页大小")
    private Integer size = 10;
}
