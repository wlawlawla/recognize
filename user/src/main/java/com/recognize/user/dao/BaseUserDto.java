package com.recognize.user.dao;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BaseUserDto {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    @NotBlank
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 全名
     */
    @NotBlank
    private String fullName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String callNumber;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 部门id
     */
    private List<Long> departmentIds;

    /**
     * 角色id
     */
    private List<Long> roleIds;

}
