package com.recognize.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

public class ObjectMapperUtil {

    public static final ObjectMapper getObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .serializers(new LocalDateSerializer(DateUtils.ISO_DATE_FORMAT)
                        , new LocalDateTimeSerializer(DateUtils.ISO_DATETIME_FORMAT))
                .deserializers(new LocalDateDeserializer(DateUtils.ISO_DATE_FORMAT),
                        new LocalDateTimeDeserializer(DateUtils.ISO_DATETIME_FORMAT))
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd")).build();
    }
}
