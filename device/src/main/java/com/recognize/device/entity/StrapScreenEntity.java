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
@TableName(value = "strap_screen")
public class StrapScreenEntity implements Serializable {

    @TableId(value = "screen_id", type = IdType.AUTO)
    private Long screenId;

    @TableField(value = "device_id")
    private Long deviceId;

    @TableField(value = "screen_type")
    private Integer screenType;

    @TableField(value = "screen_name")
    private String screenName;

    @TableField(value = "soft_type")
    private Integer softType;

    @TableField(value = "soft_page")
    private Integer softPage;

    @TableField(value = "row_num")
    private Integer rowNumber;

    @TableField(value = "col_num")
    private Integer columnNumber;

    @TableField(value = "strap_num")
    private Integer strapNumber;

    @TableField(value = "remark")
    private String remark;

    @TableField(value = "attachment_id")
    private Long attachmentId;

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