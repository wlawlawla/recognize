package com.recognize.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VOUtil {

    public static final <T, E> T getVO(Class<T> t, E s) {
        return getVO(t, s, BeanUtils.getPropertyDescriptors(t));
    }

    private static final <T, E> T getVO(Class<T> t, E s, PropertyDescriptor[] propertyDescriptors) {
        if (s == null) {
            return null;
        }
        try {
            T temp = t.newInstance();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor targetPd = propertyDescriptors[i];
                try {
                    PropertyDescriptor sourcePd = new PropertyDescriptor(targetPd.getName(), s.getClass());
                    if (sourcePd != null) {
                        Method writeMethod = targetPd.getWriteMethod();
                        Method readMethod = sourcePd.getReadMethod();
                        if (writeMethod != null && readMethod != null) {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                                Object value = readMethod.invoke(s);
                                writeMethod.invoke(temp, value);
                            } else if (sourcePd.getPropertyType() == String.class && targetPd.getPropertyType() == LocalDate.class) {
                                Object value = readMethod.invoke(s);
                                if (value != null) {
                                    String val = value.toString();
                                    LocalDate localDate;
                                    if (val.length() > 8) {
                                        localDate = DateUtils.getISODateFromNumber(val.substring(0, 8));
                                    } else {
                                        localDate = DateUtils.getISODateFromNumber(val);
                                    }
                                    writeMethod.invoke(temp, localDate);
                                }
                            } else if (sourcePd.getPropertyType() == LocalDate.class && targetPd.getPropertyType() == String.class) {
                                Object value = readMethod.invoke(s);
                                if (value != null) {
                                    LocalDate localDate = LocalDate.parse(value.toString());
                                    writeMethod.invoke(temp, DateUtils.getNumberStrDate(localDate));
                                }
                            }
                        }
                    }
                } catch (Exception e1) {
                    //ingore
                }
            }
            return temp;
        } catch (InstantiationException e1) {
            log.error(ExceptionUtils.getStackTrace(e1));
        } catch (IllegalAccessException e1) {
            log.error(ExceptionUtils.getStackTrace(e1));
        }
        return null;
    }

    public static final <T, E> List<T> getVOList(Class<T> t, List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<T> vos = new ArrayList<>(list.size());
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(t);
        for (E e : list) {
            vos.add(getVO(t, e, propertyDescriptors));
        }
        return vos;
    }


}
