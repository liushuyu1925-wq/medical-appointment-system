package org.example.springboot.enumClass;

import lombok.Getter;

@Getter
public enum NoticeActionTypeEnum {
    NOTICE_CREATE("NOTICE_CREATE", "通知生成"),
    NOTICE_READ("NOTICE_READ", "通知已读"),
    NOTICE_READ_ALL("NOTICE_READ_ALL", "全部已读"),
    REMINDER_SCAN("REMINDER_SCAN", "提醒任务扫描"),
    REMINDER_CREATE("REMINDER_CREATE", "提醒通知生成"),
    SCHEDULE_CANCEL_SCAN("SCHEDULE_CANCEL_SCAN", "停诊通知扫描");

    private final String code;
    private final String description;

    NoticeActionTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
