package org.example.springboot.enumClass;

import lombok.Getter;

@Getter
public enum NoticeActionResultEnum {
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    SKIPPED("SKIPPED", "跳过");

    private final String code;
    private final String description;

    NoticeActionResultEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
