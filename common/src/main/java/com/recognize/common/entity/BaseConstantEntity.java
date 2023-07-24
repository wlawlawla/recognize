package com.recognize.common.entity;

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
@TableName(value = "base_constant")
public class BaseConstantEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "type")
    private String type;

    @TableField(value = "code")
    private String code;

    @TableField(value = "value")
    private String value;

    @TableField(value = "p_id")
    private String parentId;

    @TableField(value = "p_code")
    private String parentCode;

    @TableField(value = "order_num")
    private String orderNumber;

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
