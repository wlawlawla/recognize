package com.recognize.task.entity;

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
@TableName(value = "task_recognize")
public class TaskRecognizeEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "task_id")
    private Long taskId;

    @TableField(value = "device_id")
    private Long deviceId;

    @TableField(value = "screen_id")
    private Long screenId;

    @TableField(value = "recognize_num")
    private Integer recognizeNumber;

    @TableField(value = "strap_num")
    private Integer strapNumber;

    @TableField(value = "error_num")
    private Integer errorNumber;

    @TableField(value = "attachment_id")
    private Long attachmentId;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "create_by")
    private Long createBy;

    @TableField(value = "updt_time")
    private LocalDateTime updateTime;

    @TableField(value = "updt_by")
    private Long updateBy;

}