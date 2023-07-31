package com.recognize.device.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StrapScreenVO {

    /**
     * 屏幕id
     */
    private Long screenId;

    /**
     * 设备id
     */
    @NotNull
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 站点名称
     */
    private String stationName;

    /**
     * 屏幕类型
     */
    private Integer screenType;

    /**
     * 屏幕类型
     */
    private String screenTypeStr;

    /**
     * 屏幕名称
     */
    @NotNull
    private String screenName;

    /**
     * 软压板类别
     */
    private Integer softType;

    /**
     * 软压板类别
     */
    private String softTypeStr;

    /**
     * 软压板页码
     */
    private Integer softPage;

    /**
     * 压板行数
     */
    private Integer rowNumber;

    /**
     * 压板列数
     */
    private Integer columnNumber;

    /**
     * 压板数量
     */
    private Integer strapNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 图片
     */
    private Long attachmentId;

    /**
     * 压板详细信息
     */
    private List<StrapDetailVO> strapDetailVOList;

}
