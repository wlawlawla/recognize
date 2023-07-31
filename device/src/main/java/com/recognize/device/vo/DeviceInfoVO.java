package com.recognize.device.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeviceInfoVO {

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编号
     */
    private String deviceNum;

    /**
     * 设备RFID
     */
    private String deviceRFID;

    /**
     * 站点id
     */
    private Long stationId;

    /**
     * 站点名称
     */
    private String stationName;

    /**
     * 排序
     */
    private Integer orderNumber;

    /**
     * 压板屏信息
     */
    private List<StrapScreenVO> strapScreenVOList;
}
