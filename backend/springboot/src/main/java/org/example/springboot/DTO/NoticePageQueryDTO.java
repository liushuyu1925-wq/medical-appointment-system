package org.example.springboot.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通知分页查询参数")
public class NoticePageQueryDTO {
    @Schema(description = "接收人关键字")
    private String recipientKeyword;

    @Schema(description = "通知类型")
    private String noticeType;

    @Schema(description = "已读状态")
    private Integer readStatus;

    @Schema(description = "发送状态")
    private Integer sendStatus;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "当前页")
    private Integer currentPage = 1;

    @Schema(description = "每页大小")
    private Integer size = 10;
}
