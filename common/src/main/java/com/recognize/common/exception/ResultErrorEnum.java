package com.recognize.common.exception;

import org.springframework.http.HttpStatus;

public enum ResultErrorEnum {

    BAD_REQUEST("000001", HttpStatus.BAD_REQUEST.value(), "请求参数有误"),
    UNAUTHORIZED("000002", HttpStatus.UNAUTHORIZED.value(), "当前请求需要用户验证"),
    NOT_FOUND("000003", HttpStatus.NOT_FOUND.value(), "请求失败，请求的资源未被在服务器上发现"),
    METHOD_NOT_ALLOWED("000004", HttpStatus.METHOD_NOT_ALLOWED.value(), "指定的请求方法不能被用于请求相应的资源"),
    REQUEST_TIMEOUT("000005", HttpStatus.REQUEST_TIMEOUT.value(), "请求超时"),
    UNSUPPORTED_MEDIA_TYPE("000006", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "请求中提交的实体并不是服务器中所支持的格式"),
    INTERNAL_SERVER_ERROR("000007", HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器异常"),
    BAD_GATEWAY("000007", HttpStatus.BAD_GATEWAY.value(), "服务器异常"),
    UNPROCESSABLE_ENTITY("000008", HttpStatus.UNPROCESSABLE_ENTITY.value(), "参数验证失败"),
    FORBIDDEN("000009", HttpStatus.FORBIDDEN.value(), "用户验证失败"),
    CONFLICT("000010", HttpStatus.CONFLICT.value(), "数据冲突"),
    NOT_ACCEPTABLE("000011", HttpStatus.NOT_ACCEPTABLE.value(), "不允许访问");

    private final String code;
    private final int httpStatus;
    private String message;

    ResultErrorEnum(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static ResultErrorEnum getResultErrorEnum(int httpStatus) {
        for (ResultErrorEnum errorEnum : values()) {
            if (errorEnum.getHttpStatus() == httpStatus) {
                return errorEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}