package com.recognize.device.parameter;

import lombok.Data;

@Data
public class StrapSearchParameter {

    /**
     * 压板id
     */
    private Long strapId;

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

}
