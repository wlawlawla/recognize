package com.recognize.device.parameter;

import lombok.Data;

@Data
public class DeviceSearchParameter {

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 站所id
     */
    private Long stationId;

    /**
     * 设备编号
     */
    private String deviceNum;


}
