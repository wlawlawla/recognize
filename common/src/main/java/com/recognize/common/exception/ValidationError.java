package com.recognize.common.exception;

import java.io.Serializable;

public class ValidationError implements Serializable {

    private static final long serialVersionUID = 7078224135717751333L;

    private String resource = "Issue";

    private String field;

    private Enum code;

    private ValidationError(String field, Enum code) {
        this.field = field;
        this.code = code;
    }

    /**
     * 查不到数据
     */
    public static ValidationError setMissing(String field) {
        return new ValidationError(field, ValidationErrorCode.missing);
    }

    /**
     * 参数不满足
     */
    public static ValidationError setMissingField(String field) {
        return new ValidationError(field, ValidationErrorCode.missing_field);
    }

    /**
     * 参数值不匹配
     *
     * @param field
     * @return
     */
    public static ValidationError setInvalid(String field) {
        return new ValidationError(field, ValidationErrorCode.invalid);
    }

    /**
     * 已经存在了
     * @param field
     * @return
     */
    public static ValidationError setAlreadyExists(String field) {
        return new ValidationError(field, ValidationErrorCode.already_exists);
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Enum getCode() {
        return code;
    }

    public void setCode(Enum code) {
        this.code = code;
    }

    enum ValidationErrorCode {
        missing, missing_field, invalid, already_exists
    }
}