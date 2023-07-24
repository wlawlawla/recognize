package com.recognize.common.exception;

import java.io.Serializable;

public class ResultErrorException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 6673052920163357237L;

    private String message;

    private ValidationError error;

    private String code;

    public ResultErrorException(String code, String message) {
        super(code + " " + message);
        this.code = code;
        this.message = message;
    }

    public ResultErrorException(ResultErrorEnum resultErrorEnum) {
        this(resultErrorEnum.getCode(), resultErrorEnum.getMessage());
    }

    public ResultErrorException(ValidationError error) {
        this(ResultErrorEnum.UNPROCESSABLE_ENTITY);
        this.error = error;
    }

    public ValidationError getError() {
        return error;
    }

    public void setError(ValidationError error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
