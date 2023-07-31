package com.recognize.device.service;

import com.recognize.device.vo.DeviceInfoVO;
import com.recognize.device.vo.StationInfoVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IDeviceInfoService {

    /**
     * 添加设备
     * @param deviceInfoVO
     * @param currentUser
     * @return
     */
    DeviceInfoVO addDevice(DeviceInfoVO deviceInfoVO, BaseUserVO currentUser);

    /**
     * 添加站点
     * @param stationInfoVO
     * @param currentUser
     * @return
     */
    StationInfoVO addStation(StationInfoVO stationInfoVO, BaseUserVO currentUser);

    /**
     * 导入设备、压板屏、压板
     * @param file
     * @param currentUser
     * @return
     * @throws IOException
     */
    List<DeviceInfoVO> importDeviceInfo(MultipartFile file, BaseUserVO currentUser)throws IOException;

    /**
     * 获取设备信息
     * @param deviceId
     * @return
     */
    DeviceInfoVO getDeviceInfoById(Long deviceId);

    /**
     * 更新设备信息
     * @param deviceInfoVO
     * @param currentUser
     * @return
     */
    DeviceInfoVO updateDeviceInfo(DeviceInfoVO deviceInfoVO, BaseUserVO currentUser);

    /**
     * 逻辑删除设备信息 并将挂在下面的压板信息也逻辑删除
     * @param deviceId
     * @param currentUser
     * @return
     */
    String deleteDevice(Long deviceId, BaseUserVO currentUser);

}
