package com.yupi.usercenter.common;

/**
 * @author ljs
 * @date 2022-06-01
 * @description
 */
public class ResultUtils {

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }

    public static BaseResponse error(ErrorCodeEnum errorCodeEnum) {
        return new BaseResponse<>(errorCodeEnum);
    }

    public static BaseResponse error(ErrorCodeEnum errorCodeEnum, String message, String description) {
        return new BaseResponse<>(errorCodeEnum.getCode(), null, message, description);
    }

    public static BaseResponse error(ErrorCodeEnum errorCodeEnum, String description) {
        return new BaseResponse<>(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), description);
    }

}
