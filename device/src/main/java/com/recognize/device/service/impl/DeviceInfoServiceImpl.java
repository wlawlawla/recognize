package com.recognize.device.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.common.common.AttachmentType;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.constant.BaseConstants;
import com.recognize.common.exception.ResultErrorEnum;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.exception.ValidationError;
import com.recognize.common.service.IAttachmentService;
import com.recognize.common.util.VOUtil;
import com.recognize.common.vo.AttachmentVO;
import com.recognize.common.vo.PageVO;
import com.recognize.device.entity.DeviceInfoEntity;
import com.recognize.device.entity.StationInfoEntity;
import com.recognize.device.mapper.DeviceInfoMapper;
import com.recognize.device.mapper.StationInfoMapper;
import com.recognize.device.parameter.DeviceSearchParameter;
import com.recognize.device.service.IDeviceInfoService;
import com.recognize.device.service.IStrapService;
import com.recognize.device.util.DeviceImageUtil;
import com.recognize.device.util.DeviceImportUtil;
import com.recognize.device.vo.DeviceInfoVO;
import com.recognize.device.vo.StationInfoVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.vo.BaseUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@TranslationalService
public class DeviceInfoServiceImpl implements IDeviceInfoService {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private StationInfoMapper stationInfoMapper;

    @Autowired
    private IStrapService strapService;

    @Autowired
    private IAttachmentService attachmentService;

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

        File image = new File(deviceInfoEntity.getDeviceName() + ".jpg");
        DeviceImageUtil.generateImage(deviceInfoEntity.getDeviceId().toString(), image);
        AttachmentVO attachmentVO = attachmentService.uploadAttachment(image, AttachmentType.DEVICE_IMAGE.getType(), deviceInfoEntity.getDeviceId());

        if (attachmentVO != null && attachmentVO.getId() != null) {
            deviceInfoEntity.setAttachmentId(attachmentVO.getId());
            deviceInfoVO.setAttachmentId(attachmentVO.getId());
            deviceInfoMapper.updateById(deviceInfoEntity);
        }

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

        fullDeviceInfoVO(deviceInfoVO);

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
        //组装压板屏幕列表，不包含压板定值
        deviceInfoVO.setStrapScreenVOList(strapService.getStrapScreenListByDeviceId(deviceId));
        fullDeviceInfoVO(deviceInfoVO);
        return deviceInfoVO;
    }

    /**
     * 组装设备信息:压板统计、二维码地址
     * @param deviceInfoVO
     */
    private void fullDeviceInfoVO(DeviceInfoVO deviceInfoVO){
        if (deviceInfoVO == null || CollectionUtils.isEmpty(deviceInfoVO.getStrapScreenVOList())){
            return;
        }

        List<StrapScreenVO> hardScreenList = deviceInfoVO.getStrapScreenVOList().stream().filter(strapScreenVO -> BaseConstants.STRAP_TYPE_HARD.equals(strapScreenVO.getScreenType())).collect(Collectors.toList());
        List<StrapScreenVO> softScreenList = deviceInfoVO.getStrapScreenVOList().stream().filter(strapScreenVO -> BaseConstants.STRAP_TYPE_SOFT.equals(strapScreenVO.getScreenType())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(hardScreenList)){
            hardScreenList.forEach(hardScreen -> {
                deviceInfoVO.setHardNumber(deviceInfoVO.getHardNumber() + (hardScreen.getStrapNumber() == null ? 0 : hardScreen.getStrapNumber()));
                deviceInfoVO.setHardColNumber(deviceInfoVO.getHardColNumber() + (hardScreen.getColumnNumber() == null ? 0 : hardScreen.getColumnNumber()));
                deviceInfoVO.setHardRowNumber(deviceInfoVO.getHardRowNumber() + (hardScreen.getRowNumber() == null ? 0 : hardScreen.getRowNumber()));
            });
        }

        if (CollectionUtils.isNotEmpty(softScreenList)){
            deviceInfoVO.setSoftScreenNumber(softScreenList.size());
            softScreenList.forEach(softScreen -> {
                deviceInfoVO.setSoftNumber(deviceInfoVO.getSoftNumber() + (softScreen.getStrapNumber() == null ? 0 : softScreen.getStrapNumber()));
            });
        }

        deviceInfoVO.setImageUrl();

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

    @Override
    public List<StationInfoVO> getStationList(){
        List<StationInfoEntity> stationInfoEntityList = stationInfoMapper.findAll();

        return VOUtil.getVOList(StationInfoVO.class, stationInfoEntityList);
    }


    @Override
    public List<DeviceInfoVO> getDeviceInfoByIdIn(List<Long> deviceIds){
        if (CollectionUtils.isEmpty(deviceIds)){
            return Collections.emptyList();
        }

        List<DeviceInfoEntity> deviceInfoEntityList = deviceInfoMapper.findByDeviceIdIn(deviceIds);

        return VOUtil.getVOList(DeviceInfoVO.class, deviceInfoEntityList);
    }

    @Override
    public StationInfoVO getStationById(Long stationId){
        if (stationId == null){
            return null;
        }
        return VOUtil.getVO(StationInfoVO.class, stationInfoMapper.findById(stationId));
    }

    @Override
    public PageVO<DeviceInfoVO> searchDevice(Pageable pageable, DeviceSearchParameter searchParameter){
        Page page = new Page();
        page.setCurrent(pageable.getPageNumber());
        page.setSize(pageable.getPageSize());

        Page<DeviceInfoEntity> deviceInfoEntityPage = deviceInfoMapper.searchDevice(page, searchParameter);

        PageVO<DeviceInfoVO> deviceInfoVOPageVO = new PageVO<>();
        deviceInfoVOPageVO.setPage((int)deviceInfoEntityPage.getCurrent());
        deviceInfoVOPageVO.setSize((int)deviceInfoEntityPage.getSize());
        deviceInfoVOPageVO.setTotal(deviceInfoEntityPage.getTotal());
        List<DeviceInfoVO> deviceInfoVOList = VOUtil.getVOList(DeviceInfoVO.class, deviceInfoEntityPage.getRecords());

        if (CollectionUtils.isNotEmpty(deviceInfoVOList)){

            Map<Long, String> stationNameMap = new HashMap<>();
            List<Long> stationIds = deviceInfoVOList.stream().map(DeviceInfoVO::getStationId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(stationIds)){
                List<StationInfoEntity> stations = stationInfoMapper.findByStationIdIn(stationIds);
                if (CollectionUtils.isNotEmpty(stations)){
                    stationNameMap.putAll(stations.stream().collect(Collectors.toMap(StationInfoEntity::getStationId, StationInfoEntity::getStationName)));
                }
            }

            deviceInfoVOList.forEach(deviceInfoVO -> {
                fullDeviceInfoVO(deviceInfoVO);
                deviceInfoVO.setStationName(stationNameMap.get(deviceInfoVO.getStationId()));
            });
        }

        deviceInfoVOPageVO.setItems(deviceInfoVOList);
        return deviceInfoVOPageVO;
    }



}
