package com.recognize.device.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StrapDetailVO {

    /**
     * 压板id
     */
    private Long strapId;

    /**
     * 压板屏id
     */
    @NotNull
    private Long screenId;

    /**
     * 压板屏名称
     */
    private String screenName;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 站点名称
     */
    private String stationName;

    /**
     * 压板名称
     */
    private String strapName;

    /**
     * 压板值
     */
    private String strapValue;

    /**
     * 压板位置
     */
    private Integer strapPosition;
}
