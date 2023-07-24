package com.recognize.user.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class  BaseUserVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 全名
     */
    private String fullName;

    /**
     * 手机号
     */
    private String callNumber;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 权限
     */
    private List<String> authorities;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
