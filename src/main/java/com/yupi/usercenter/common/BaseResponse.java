package com.yupi.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ljs
 * @date 2022-06-01
 * @description 通用返回类
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, null);
    }

    public BaseResponse(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getCode(), null, errorCodeEnum.getMessage(), errorCodeEnum.getDescription());
    }

}
