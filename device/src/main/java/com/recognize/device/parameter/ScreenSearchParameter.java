package com.recognize.device.parameter;

import lombok.Data;

@Data
public class ScreenSearchParameter {

    /**
     * 压板屏id
     */
    private Long screenId;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 站所id
     */
    private Long stationId;

    /**
     * 设备名称
     */
    private String screenName;


}
