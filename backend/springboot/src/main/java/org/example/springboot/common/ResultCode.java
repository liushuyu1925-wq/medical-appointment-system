package org.example.springboot.common;

public enum ResultCode {
    SUCCESS("200", "操作成功"),
    ERROR("-1", "操作失败"),
    VALIDATE_FAILED("400", "参数检验失败"),
    UNAUTHORIZED("401", "暂未登录或token已经过期"),
    FORBIDDEN("403", "没有相关权限"),
    NOT_FOUND("404", "资源不存在"),
    SYSTEM_ERROR("500", "系统错误");

    private String code;
    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
} 
