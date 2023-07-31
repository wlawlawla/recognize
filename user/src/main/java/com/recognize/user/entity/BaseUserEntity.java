package com.recognize.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "base_user")
public class BaseUserEntity implements Serializable {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "password")
    private String password;

    @TableField(value = "nick_name")
    private String nickName;

    @TableField(value = "full_name")
    private String fullName;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "call_num")
    private String callNumber;

    @TableField(value = "is_del")
    private Integer del;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "create_by")
    private Long createBy;

    @TableField(value = "updt_time")
    private LocalDateTime updateTime;

    @TableField(value = "updt_by")
    private Long updateBy;

    /**
     * 用户权限
     */
    private Collection<GrantedAuthority> authorities;


}