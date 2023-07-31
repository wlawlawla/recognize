package com.recognize.device.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StationInfoVO {

    /**
     * 站点id
     */
    private Long stationId;

    /**
     * 站点名称
     */
    @NotNull
    private String stationName;

    /**
     * 设备列表
     */
    private List<DeviceInfoVO> deviceInfoVOList;
}
