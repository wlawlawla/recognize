package com.recognize.task.vo;

import com.recognize.device.vo.DeviceInfoVO;
import com.recognize.user.vo.BaseUserVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskInfoVO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务编号
     */
    private String taskNumber;

    /**
     * 巡检周期
     */
    private String taskCycle;

    /**
     * 备注
     */
    private String remark;

    /**
     * 站点id
     */
    private Long stationId;

    /**
     * 站点名称
     */
    private String stationName;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime endTime;

    /**
     * 异常数量
     */
    private Integer errorNumber;

    /**
     * 巡检结果
     */
    private String taskResult;

    /**
     * 任务创建人id
     */
    private Long createBy;

    /**
     * 负责人
     */
    private List<BaseUserVO> directors;

    /**
     * 工作组成员
     */
    private List<BaseUserVO> workers;

    /**
     * 设备
     */
    private List<DeviceInfoVO> devices;

}
