package com.recognize.common.util;

import com.recognize.common.exception.ResultErrorEnum;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.vo.ErrorMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ResultErrorExceptionUtil {

    public static HttpStatus getHttpStatus(ResultErrorException resultErrorException) {
        for (ResultErrorEnum errorEnum : ResultErrorEnum.values()) {
            if (errorEnum.getCode().equals(resultErrorException.getCode())) {
                int status = errorEnum.getHttpStatus();
                return HttpStatus.valueOf(status);
            }
        }
        return null;
    }

    public static Map<String, Object> getErrorMap(ResultErrorException resultErrorException) {
        ErrorMsgVO errorMsgVO = new ErrorMsgVO(resultErrorException.getMessage(), resultErrorException.getError());
        Map<String, Object> map = new LinkedHashMap<>();
        for (Field field : ErrorMsgVO.class.getDeclaredFields()) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object obj = field.get(errorMsgVO);
                if (obj != null) {
                    map.put(field.getName(), field.get(errorMsgVO));
                }
            } catch (IllegalAccessException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return map;
    }
}
