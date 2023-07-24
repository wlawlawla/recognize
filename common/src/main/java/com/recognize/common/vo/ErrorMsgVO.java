package com.recognize.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.recognize.common.exception.ValidationError;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMsgVO implements Serializable {

    private static final long serialVersionUID = -3720035154593692473L;

    private final Long timestamp = System.currentTimeMillis();

    private String message;

    private ValidationError errors;

    public ErrorMsgVO(String message) {
        this.message = message;
    }

    public ErrorMsgVO(String message, ValidationError errors) {
        this.message = message;
        this.errors = errors;
    }
}
