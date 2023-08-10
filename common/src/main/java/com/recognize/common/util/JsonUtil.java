package com.recognize.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "unchecked", "rawtypes"})
@Slf4j
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtil.getObjectMapper();
    private static final Pattern GETTER_METHOD_PATTERN = Pattern.compile("^(?:get|is)(.+)$");

    static {
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 将对象序列化为json字符串.
     *
     * @param object 要转换的对象
     * @return 对象序列化后的json字符串
     */
    public static String toJsonString(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            log.error("生成对象json字符串失败，对象：{}", object);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象序列化为json数组.
     *
     * @param object 要转换的对象
     * @return 对象序列化后的json字符串
     */
    public static byte[] toJsonBytes(Object object){
        if (object == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        } catch (IOException e) {
            log.error( "生成对象json字符串失败，对象：{}", object);
            throw new RuntimeException(e);
        }
    }

    /**
     * json反序列化为map
     *
     * @param jsonString json字符串
     * @return 反序列化后的map
     */
    public static Map<String, Object> toMap(String jsonString) {
        return fromJson(jsonString, Map.class);
    }

    /**
     * json反序列化为map
     *
     * @param object 对象
     * @return 反序列化后的map
     */
    public static Map<String, Object> toMap(Object object) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
            MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
            Map<String, Object> map = new HashMap<>(16);

            for (MethodDescriptor methodDescriptor : methodDescriptors) {
                String methodName = methodDescriptor.getName();
                Matcher matcher = GETTER_METHOD_PATTERN.matcher(methodName);

                if (matcher.matches()) {
                    String key = matcher.group(1);
                    key = key.substring(0, 1).toLowerCase() + key.substring(1);
                    Method getter = methodDescriptor.getMethod();
                    getter.setAccessible(true);
                    Object value = getter.invoke(object);

                    map.put(key, value);
                }
            }

            return map;
        } catch (Exception e) {
            log.error("对象转map时失败，对象：{}", object);
            throw new RuntimeException(e);
        }
    }

    /**
     * json转list
     *
     * @param json  json字符串
     * @param clazz 类信息
     * @return list集合
     */
    public static <E> List<E> toList(String json, Class<E> clazz) {
        List<E> list;

        try {
            list = OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz));

            if (list == null) {
                list = new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("转换json字符串为list集合失败，json为：{}，类名为：{}", json, clazz.getName());
            throw new RuntimeException(e);
        }

        return list;
    }

    /**
     * json转换为对象.
     *
     * @param jsonString json字符串
     * @param clazz      对象类型
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (jsonString == null || "".equals(jsonString.trim())) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.error("转换json字符串为对象失败，json为：{}，类名为：{}", jsonString, clazz.getName());
            throw new RuntimeException(e);
        }
    }

    /**
     * json转换为对象.
     *
     * @param jsonString json字符串
     * @param valueTypeRef 对象类型
     */
    public static <T> T fromJson(String jsonString,TypeReference<T> valueTypeRef){
        if (jsonString == null || "".equals(jsonString.trim())) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(jsonString, valueTypeRef);
        } catch (IOException e) {
            log.error("转换json字符串为对象失败，json为：{}，类名为：{}", jsonString, valueTypeRef.getType());
            throw new RuntimeException(e);
        }
    }

    /**
     * @param json 要转换的json字符串
     * @return json结点
     */
    public static JsonNode toNode(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, JsonNode.class);
        } catch (Exception e) {
            log.error("转换json字符串为json结点失败，json为：{}", json);
            throw new RuntimeException(e);
        }
    }
}
