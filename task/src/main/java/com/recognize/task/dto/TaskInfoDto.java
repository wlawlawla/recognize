package com.recognize.task.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskInfoDto {

    /**
     * 站点id
     */
    private Long stationId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 负责人id
     */
    private List<Long> directorIds;

    /**
     * 工作组成员id
     */
    private List<Long> workerIds;

    /**
     * 巡检周期
     */
    private String taskCycle;

    /**
     * 巡检设备id
     */
    private List<Long> deviceIds;

    /**
     * 备注
     */
    private String remark;
}
