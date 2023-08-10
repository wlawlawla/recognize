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
@TableName(value = "task_detail")
public class TaskDetailEntity implements Serializable {

    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    @TableField(value = "task_type")
    private String taskType;

    @TableField(value = "task_name")
    private String taskName;

    @TableField(value = "task_cycle")
    private String taskCycle;

    @TableField(value = "task_num")
    private String taskNumber;

    @TableField(value = "remark")
    private String remark;

    @TableField(value = "station_id")
    private Long stationId;

    @TableField(value = "status")
    private String status;

    @TableField(value = "start_time")
    private LocalDateTime startTime;

    @TableField(value = "end_time")
    private LocalDateTime endTime;

    @TableField(value = "error_num")
    private Integer errorNumber;

    @TableField(value = "task_result")
    private String taskResult;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "create_by")
    private Long createBy;

    @TableField(value = "updt_time")
    private LocalDateTime updateTime;

    @TableField(value = "updt_by")
    private Long updateBy;

}