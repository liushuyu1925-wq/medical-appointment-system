package org.example.springboot.enumClass;

import lombok.Getter;

@Getter
public enum NoticeTypeEnum {
    APPOINTMENT_SUCCESS("APPOINTMENT_SUCCESS", "预约成功通知"),
    APPOINTMENT_CANCEL("APPOINTMENT_CANCEL", "预约取消通知"),
    RESCHEDULE_SUCCESS("RESCHEDULE_SUCCESS", "改约成功通知"),
    VISIT_REMINDER("VISIT_REMINDER", "就诊提醒通知"),
    SCHEDULE_CANCEL("SCHEDULE_CANCEL", "停诊通知");

    private final String code;
    private final String description;

    NoticeTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
