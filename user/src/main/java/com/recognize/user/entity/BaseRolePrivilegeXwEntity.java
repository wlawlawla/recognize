package com.recognize.user.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "base_role_privilege_xw")
public class BaseRolePrivilegeXwEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "role_id")
    private Long roleId;

    @TableField(value = "privilege_id")
    private Long privilegeId;

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

}