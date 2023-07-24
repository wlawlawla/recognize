package com.recognize.user.util;

import com.recognize.user.entity.BaseUserEntity;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    /**
     * 私有化构造器
     */
    private SecurityUtil(){}

    /**
     * 获取当前用户信息
     */
    public static BaseUserVO getUserInfo(){
        BaseUserVO userDetails = (BaseUserVO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails;
    }
    /**
     * 获取当前用户ID
     */
    public static Long getUserId(){
        return getUserInfo().getUserId();
    }
    /**
     * 获取当前用户账号
     */
    public static String getUserName(){
        return getUserInfo().getUserName();
    }
}
