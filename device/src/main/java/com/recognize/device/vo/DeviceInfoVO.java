package com.recognize.device.vo;

import com.recognize.common.constant.BaseConstants;
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
     * 硬压板行数
     */
    private Integer hardRowNumber = 0;

    /**
     * 硬压板列数
     */
    private Integer hardColNumber = 0;

    /**
     * 硬压板总数
     */
    private Integer hardNumber = 0;

    /**
     * 软压板屏数量
     */
    private Integer softScreenNumber = 0;

    /**
     * 软压板总数
     */
    private Integer softNumber = 0;

    /**
     * 二维码地址
     */
    private String imageUrl;

    /**
     * 二维码id
     */
    private Long  attachmentId;

    /**
     * 压板屏信息
     */
    private List<StrapScreenVO> strapScreenVOList;

    public void setImageUrl(){
        if (this.attachmentId != null){
            this.imageUrl = BaseConstants.IMAGE_URL_PREFIX + this.attachmentId;
        }
    }
}
