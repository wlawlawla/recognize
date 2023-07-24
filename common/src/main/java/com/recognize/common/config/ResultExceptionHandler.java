package com.recognize.common.config;

import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.util.ResultErrorExceptionUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice(basePackages = "com.recognize")
@ResponseBody
public class ResultExceptionHandler {

    @ExceptionHandler({ResultErrorException.class})
    public ResponseEntity resultErrorExceptionHandler(ResultErrorException resultErrorException) {
        Map<String, Object> map = ResultErrorExceptionUtil.getErrorMap(resultErrorException);
        HttpStatus httpStatus = ResultErrorExceptionUtil.getHttpStatus(resultErrorException);
        return new ResponseEntity<>(map, httpStatus);
    }

}
