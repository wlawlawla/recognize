package com.recognize.device.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.constant.BaseConstants;
import com.recognize.common.exception.ResultErrorEnum;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.exception.ValidationError;
import com.recognize.common.util.VOUtil;
import com.recognize.device.entity.DeviceInfoEntity;
import com.recognize.device.entity.StationInfoEntity;
import com.recognize.device.mapper.DeviceInfoMapper;
import com.recognize.device.mapper.StationInfoMapper;
import com.recognize.device.service.IDeviceInfoService;
import com.recognize.device.service.IStrapService;
import com.recognize.device.util.DeviceImportUtil;
import com.recognize.device.vo.DeviceInfoVO;
import com.recognize.device.vo.StationInfoVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.vo.BaseUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@TranslationalService
public class DeviceInfoServiceImpl implements IDeviceInfoService {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private StationInfoMapper stationInfoMapper;

    @Autowired
    private IStrapService strapService;

    @Override
    public DeviceInfoVO addDevice(DeviceInfoVO deviceInfoVO, BaseUserVO currentUser){
        if (deviceInfoVO == null){
            return null;
        }

        if (StringUtils.isNotBlank(deviceInfoVO.getStationName()) && deviceInfoVO.getStationId() == null){
            //如果只有站点名称没有站点id，就去新增站点（会先根据名称查询是否已存在该站点，如果已存在就返回查询结果，若不存在就创建并返回）
            StationInfoVO stationInfoVO = new StationInfoVO();
            stationInfoVO.setStationName(deviceInfoVO.getStationName());
            stationInfoVO = addStation(stationInfoVO, currentUser);
            deviceInfoVO.setStationId(stationInfoVO.getStationId());
        }

        DeviceInfoEntity deviceInfoEntity = VOUtil.getVO(DeviceInfoEntity.class, deviceInfoVO);

        deviceInfoEntity.setCreateBy(currentUser.getUserId());
        deviceInfoEntity.setUpdateBy(currentUser.getUserId());

        //保存设备信息，拿到设备id
        deviceInfoMapper.insert(deviceInfoEntity);
        List<StrapScreenVO> strapScreenVOList = new ArrayList<>();

        //把设备id填充到压板屏幕对象里面，然后保存压板屏幕
        if (CollectionUtils.isNotEmpty(deviceInfoVO.getStrapScreenVOList())){
            deviceInfoVO.getStrapScreenVOList().forEach(strapScreenVO -> {
                strapScreenVO.setDeviceId(deviceInfoEntity.getDeviceId());
                strapScreenVOList.add(strapService.addStrapScreen(strapScreenVO, currentUser));
            });
        }

        //组装响应数据
        deviceInfoVO.setDeviceId(deviceInfoEntity.getDeviceId());
        deviceInfoVO.setStrapScreenVOList(strapScreenVOList);
        if (deviceInfoVO.getStationId() != null){
            StationInfoEntity stationInfoEntity = stationInfoMapper.findById(deviceInfoVO.getStationId());
            if (stationInfoEntity != null){
                deviceInfoVO.setStationName(stationInfoEntity.getStationName());
            }
        }

        return deviceInfoVO;
    }

    @Override
    public StationInfoVO addStation(StationInfoVO stationInfoVO, BaseUserVO currentUser){
        if (stationInfoVO == null){
            return null;
        }

        StationInfoEntity stationInfoEntity = stationInfoMapper.findByStationName(stationInfoVO.getStationName());
        if (stationInfoEntity == null){
            stationInfoEntity = VOUtil.getVO(StationInfoEntity.class, stationInfoVO);
            stationInfoEntity.setCreateBy(currentUser.getUserId());
            stationInfoEntity.setUpdateBy(currentUser.getUserId());
            stationInfoMapper.insert(stationInfoEntity);
        }

        stationInfoVO.setStationId(stationInfoEntity.getStationId());

        //如果有设备信息，则把站点id组装进去，然后调用保存设备接口
        List<DeviceInfoVO> deviceInfoVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(stationInfoVO.getDeviceInfoVOList())){
            stationInfoVO.getDeviceInfoVOList().forEach(deviceInfoVO -> {
                deviceInfoVO.setStationId(stationInfoVO.getStationId());
                deviceInfoVOList.add(addDevice(deviceInfoVO, currentUser));
            });
        }
        stationInfoVO.setDeviceInfoVOList(deviceInfoVOList);

        return stationInfoVO;
    }

    @Override
    public List<DeviceInfoVO> importDeviceInfo(MultipartFile file, BaseUserVO currentUser)throws IOException {
        List<DeviceInfoVO> importDeviceList = DeviceImportUtil.convertToVO(file);
        List<DeviceInfoVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(importDeviceList)){
            importDeviceList.forEach(deviceInfoVO -> result.add(addDevice(deviceInfoVO, currentUser)));
        }

        return result;
    }

    @Override
    public DeviceInfoVO getDeviceInfoById(Long deviceId){
        if (deviceId == null){
            return null;
        }

        DeviceInfoEntity deviceInfoEntity = deviceInfoMapper.findById(deviceId);
        if (deviceInfoEntity == null){
            return null;
        }

        DeviceInfoVO deviceInfoVO = VOUtil.getVO(DeviceInfoVO.class, deviceInfoEntity);
        if (deviceInfoVO.getStationId() != null){
            StationInfoEntity stationInfoEntity = stationInfoMapper.findById(deviceInfoVO.getStationId());
            if (stationInfoEntity != null){
                deviceInfoVO.setStationName(stationInfoEntity.getStationName());
            }
        }
        //组装压板屏幕列表，不
        deviceInfoVO.setStrapScreenVOList(strapService.getStrapScreenListByDeviceId(deviceId));

        return deviceInfoVO;
    }

    @Override
    public DeviceInfoVO updateDeviceInfo(DeviceInfoVO deviceInfoVO, BaseUserVO currentUser){
        if (deviceInfoVO == null || deviceInfoVO.getDeviceId() == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

        DeviceInfoEntity deviceInfoEntity = deviceInfoMapper.findById(deviceInfoVO.getDeviceId());
        if (deviceInfoEntity == null){
            throw new ResultErrorException(ValidationError.setMissing("找不到对应的设备: deviceId = " + deviceInfoVO.getDeviceId()));
        }

        //stationId 不可修改
        deviceInfoEntity.setDeviceName(deviceInfoVO.getDeviceName());
        deviceInfoEntity.setDeviceNum(deviceInfoVO.getDeviceNum());
        deviceInfoEntity.setDeviceRFID(deviceInfoVO.getDeviceRFID());
        deviceInfoEntity.setOrderNumber(deviceInfoVO.getOrderNumber());

        deviceInfoEntity.setUpdateBy(currentUser.getUserId());
        deviceInfoEntity.setUpdateTime(LocalDateTime.now());

        deviceInfoMapper.updateById(deviceInfoEntity);

        deviceInfoVO = VOUtil.getVO(DeviceInfoVO.class, deviceInfoEntity);
        if (deviceInfoVO.getStationId() != null){
            StationInfoEntity stationInfoEntity = stationInfoMapper.findById(deviceInfoVO.getStationId());
            if (stationInfoEntity != null){
                deviceInfoVO.setStationName(stationInfoEntity.getStationName());
            }
        }

        return deviceInfoVO;
    }

    @Override
    public String deleteDevice(Long deviceId, BaseUserVO currentUser){
        String name = null;
        if (deviceId != null){
            DeviceInfoEntity deviceInfoEntity = deviceInfoMapper.findById(deviceId);
            if (deviceInfoEntity != null){
                name = deviceInfoEntity.getDeviceName();
            }
            deviceInfoEntity.setDel(BaseConstants.BASE_ENTITY_INVALID);
            deviceInfoEntity.setUpdateBy(currentUser.getUserId());
            deviceInfoEntity.setUpdateTime(LocalDateTime.now());
            deviceInfoMapper.updateById(deviceInfoEntity);

            strapService.deleteStrapScreenByDeviceId(deviceId, currentUser);
        }


        return name;
    }


}
