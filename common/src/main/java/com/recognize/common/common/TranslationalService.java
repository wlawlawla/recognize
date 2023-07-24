package com.recognize.common.common;

import org.springframework.core.annotation.AliasFor;

@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@org.springframework.stereotype.Service
@org.springframework.transaction.annotation.Transactional()
public @interface TranslationalService {

    @AliasFor(
            annotation = org.springframework.stereotype.Service.class
    )
    String value() default "";
}