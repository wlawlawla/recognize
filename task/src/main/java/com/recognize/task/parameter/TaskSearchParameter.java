package com.recognize.task.parameter;

import lombok.Data;

@Data
public class TaskSearchParameter {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 站所id
     */
    private Long stationId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务编号
     */
    private String taskNumber;


}
