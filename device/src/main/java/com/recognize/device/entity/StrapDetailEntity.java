package com.recognize.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "strap_detail")
public class StrapDetailEntity implements Serializable {

    @TableId(value = "strap_id", type = IdType.AUTO)
    private Long strapId;

    @TableField(value = "screen_id")
    private Long screenId;

    @TableField(value = "strap_name")
    private String strapName;

    @TableField(value = "strap_position")
    private Integer strapPosition;

    @TableField(value = "strap_value")
    private String strapValue;

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