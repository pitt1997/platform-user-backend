package com.yupi.usercenter.common;

/**
 * @author ljs
 * @date 2022-06-01
 * @description 错误码
 */
public enum ErrorCodeEnum {

    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    PARAMS_NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "请求数据为空", ""),
    NO_AUTH(40101, "无权限", ""),
    SYSTEM_ERROR(50000, "系统内部异常", "");

    /**
     * 状态码信息
     */
    private final int code;

    /**
     * 状态码描述
     */
    private final String message;

    private final String description;

    ErrorCodeEnum(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
