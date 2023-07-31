package com.recognize.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "device_info")
public class DeviceInfoEntity implements Serializable {

    @TableId(value = "device_id", type = IdType.AUTO)
    private Long deviceId;

    @TableField(value = "device_name")
    private String deviceName;

    @TableField(value = "device_num")
    private String deviceNum;

    @TableField(value = "device_rfid")
    private String deviceRFID;

    @TableField(value = "station_id")
    private Long stationId;

    @TableField(value = "order_num")
    private Integer orderNumber;

    @TableField(value = "is_del")
    private Integer del;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "create_by")
    private Long createBy;

    @TableField(value = "updt_time")
    private LocalDateTime updateTime;

    @TableField(value = "updt_by")
    private Long updateBy;

}