package org.example.springboot.enumClass;

import lombok.Getter;

@Getter
public enum StatusEnum {
    // 通用状态
    NORMAL(1, "正常"),
    ABNORMAL(0, "异常"),
    
    // 治疗状态
    IN_PROGRESS(1, "进行中"),
    COMPLETED(0, "已完成"),
    
    // 诊断状态
    VALID(1, "有效"),
    INVALID(0, "无效"),
    
    // 随访状态
    FINISHED(1, "已完成"),
    PENDING(0, "未完成");
    
    private final Integer code;
    private final String message;
    
    StatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public static StatusEnum getByCode(Integer code) {
        for (StatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}