package com.recognize.device.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.recognize.common.common.AttachmentType;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.constant.BaseConstants;
import com.recognize.common.exception.ResultErrorEnum;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.exception.ValidationError;
import com.recognize.common.service.IAttachmentService;
import com.recognize.common.service.IConstantService;
import com.recognize.common.util.VOUtil;
import com.recognize.common.vo.AttachmentVO;
import com.recognize.common.vo.ConstantVO;
import com.recognize.common.vo.PageVO;
import com.recognize.device.entity.DeviceInfoEntity;
import com.recognize.device.entity.StationInfoEntity;
import com.recognize.device.entity.StrapDetailEntity;
import com.recognize.device.entity.StrapScreenEntity;
import com.recognize.device.mapper.DeviceInfoMapper;
import com.recognize.device.mapper.StationInfoMapper;
import com.recognize.device.mapper.StrapDetailMapper;
import com.recognize.device.mapper.StrapScreenMapper;
import com.recognize.device.parameter.ScreenSearchParameter;
import com.recognize.device.parameter.StrapSearchParameter;
import com.recognize.device.service.IStrapService;
import com.recognize.device.vo.StrapDetailVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@TranslationalService
public class StrapServiceImpl extends ServiceImpl<StrapDetailMapper, StrapDetailEntity> implements IStrapService {

    @Autowired
    private StrapScreenMapper strapScreenMapper;

    @Autowired
    private StationInfoMapper stationInfoMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private IConstantService constantService;

    @Autowired
    private IAttachmentService attachmentService;

    Map<String, Integer> strapValueMap;
    Map<String, Integer> softValueMap;
    Map<Integer, String> softCodeMap;
    Map<Integer, String> strapCodeMap;

    @PostConstruct
    private void init(){
        List<ConstantVO> strapTypeList = constantService.getConstantByType(BaseConstants.CONSTANT_TYPE_STRAP);
        List<ConstantVO> softTypeList = constantService.getConstantByType(BaseConstants.CONSTANT_TYPE_SOFT);

        strapValueMap = strapTypeList.stream().collect(Collectors.toMap(ConstantVO::getValue, constantVO -> Integer.valueOf(constantVO.getCode())));
        softValueMap = softTypeList.stream().collect(Collectors.toMap(ConstantVO::getValue, constantVO -> Integer.valueOf(constantVO.getCode())));
        strapCodeMap = strapTypeList.stream().collect(Collectors.toMap(constantVO -> Integer.valueOf(constantVO.getCode()), ConstantVO::getValue));
        softCodeMap = softTypeList.stream().collect(Collectors.toMap(constantVO -> Integer.valueOf(constantVO.getCode()), ConstantVO::getValue));
    }

    @Override
    public StrapScreenVO addStrapScreen(StrapScreenVO strapScreenVO, BaseUserVO currentUser){
        if (strapScreenVO == null){
            return null;
        }

        if (StringUtils.isNotBlank(strapScreenVO.getScreenTypeStr())){
            strapScreenVO.setScreenType(strapValueMap.get(strapScreenVO.getScreenTypeStr()));
        }

        if (StringUtils.isNotBlank(strapScreenVO.getSoftTypeStr())){
            strapScreenVO.setSoftType(softValueMap.get(strapScreenVO.getSoftTypeStr()));
        }

        StrapScreenEntity strapScreenEntity = VOUtil.getVO(StrapScreenEntity.class, strapScreenVO);
        strapScreenEntity.setUpdateBy(currentUser.getUserId());
        strapScreenEntity.setCreateBy(currentUser.getUserId());

        strapScreenMapper.insert(strapScreenEntity);
        strapScreenVO.setScreenId(strapScreenEntity.getScreenId());


        if (CollectionUtils.isNotEmpty(strapScreenVO.getStrapDetailVOList())){
            //保存压板详细信息
            strapScreenVO.setStrapDetailVOList(addStrapDetail(strapScreenVO.getStrapDetailVOList(), strapScreenEntity.getScreenId(), currentUser));
        }

        //组装响应数据：设备名称、站点名称
        if (strapScreenVO.getDeviceId() != null){
            DeviceInfoEntity deviceInfoEntity = deviceInfoMapper.findById(strapScreenVO.getDeviceId());
            if (deviceInfoEntity != null){
                strapScreenVO.setDeviceName(deviceInfoEntity.getDeviceName());

                StationInfoEntity stationInfoEntity = stationInfoMapper.findById(deviceInfoEntity.getStationId());
                if (stationInfoEntity != null){
                    strapScreenVO.setStationName(stationInfoEntity.getStationName());
                }
            }
        }

        return strapScreenVO;
    }

    @Override
    public StrapDetailVO addStrapDetail(StrapDetailVO strapDetailVO, BaseUserVO currentUser){
        if (strapDetailVO == null || strapDetailVO.getScreenId() == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }
        List<StrapDetailVO> strapDetailVOList = addStrapDetail(Arrays.asList(strapDetailVO), strapDetailVO.getScreenId(), currentUser);
        if (CollectionUtils.isEmpty(strapDetailVOList)){
            log.error("压板信息添加失败");
            return null;
        }

        return strapDetailVOList.get(0);
    }


    /**
     * 批量保存压板信息
     * @param strapDetailVOList
     * @param screenId
     * @param currentUser
     * @return
     */
    private List<StrapDetailVO> addStrapDetail(List<StrapDetailVO> strapDetailVOList, Long screenId, BaseUserVO currentUser){
        if (CollectionUtils.isEmpty(strapDetailVOList)){
            return Collections.EMPTY_LIST;
        }

        List<StrapDetailEntity> strapDetailEntityList = VOUtil.getVOList(StrapDetailEntity.class, strapDetailVOList);
        strapDetailEntityList.forEach(strapDetailEntity -> {
            if (screenId != null){
                strapDetailEntity.setScreenId(screenId);
            }
            strapDetailEntity.setCreateBy(currentUser.getUserId());
            strapDetailEntity.setUpdateBy(currentUser.getUserId());
        });

        saveBatch(strapDetailEntityList);

        return getStrapDetailVOList(strapDetailEntityList);

    }

    /**
     * 转换成vo对象，并填充压板屏名称、设备名称、站点名称
     * @param strapDetailEntityList
     * @return
     */
    private List<StrapDetailVO> getStrapDetailVOList(List<StrapDetailEntity> strapDetailEntityList){

        List<StrapDetailVO> strapDetailVOList = VOUtil.getVOList(StrapDetailVO.class, strapDetailEntityList);

        if (CollectionUtils.isEmpty(strapDetailVOList)){
            return Collections.emptyList();
        }

        List<Long> screenIdList = strapDetailEntityList.stream().map(StrapDetailEntity::getScreenId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(screenIdList)){
            return strapDetailVOList;
        }

        Map<Long, StrapScreenEntity> screenMap = new HashMap<>();
        Map<Long, DeviceInfoEntity> deviceMap = new HashMap<>();
        Map<Long, StationInfoEntity> stationMap = new HashMap<>();

        List<Long> deviceIdList = new ArrayList<>();
        List<Long> stationIdList = new ArrayList<>();

        List<StrapScreenEntity> strapScreenEntityList = strapScreenMapper.findByScreenIdIn(screenIdList);

        //逐级查询：压板屏-设备-站点
        if (CollectionUtils.isNotEmpty(strapScreenEntityList)) {
            screenMap.putAll(strapScreenEntityList.stream().collect(Collectors.toMap(StrapScreenEntity::getScreenId, strapScreenEntity -> strapScreenEntity)));
            deviceIdList.addAll(strapScreenEntityList.stream().map(StrapScreenEntity::getDeviceId).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(deviceIdList)){
            List<DeviceInfoEntity> deviceList = deviceInfoMapper.findByDeviceIdIn(deviceIdList);
            if (CollectionUtils.isNotEmpty(deviceList)){
                deviceMap.putAll(deviceList.stream().collect(Collectors.toMap(DeviceInfoEntity::getDeviceId, deviceInfoEntity -> deviceInfoEntity)));
                stationIdList.addAll(deviceList.stream().map(DeviceInfoEntity::getStationId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
            }
        }

        if (CollectionUtils.isNotEmpty(stationIdList)){
            List<StationInfoEntity> stationInfoEntityList = stationInfoMapper.findByStationIdIn(stationIdList);
            if (CollectionUtils.isNotEmpty(stationInfoEntityList)){
                stationMap.putAll(stationInfoEntityList.stream().collect(Collectors.toMap(StationInfoEntity::getStationId, stationInfoEntity -> stationInfoEntity)));
            }
        }

        //逐级组装压板屏，设备，站点名称
        strapDetailVOList.forEach(strapDetailVO -> {
            StrapScreenEntity screenEntity = screenMap.get(strapDetailVO.getScreenId());
            if (screenEntity != null){
                strapDetailVO.setScreenName(screenEntity.getScreenName());

                DeviceInfoEntity deviceInfoEntity = deviceMap.get(screenEntity.getDeviceId());
                if (deviceInfoEntity != null){
                    strapDetailVO.setDeviceName(deviceInfoEntity.getDeviceName());

                    StationInfoEntity stationInfoEntity = stationMap.get(deviceInfoEntity.getStationId());
                    if (stationInfoEntity != null){
                        strapDetailVO.setStationName(stationInfoEntity.getStationName());
                    }
                }
            }
        });

        return strapDetailVOList;
    }

    @Override
    public List<StrapDetailVO> getStrapDetailByScreenId(Long screenId){
        if (screenId == null){
            return Collections.emptyList();
        }

        return getStrapDetailVOList(baseMapper.findByScreenId(screenId));
    }

    @Override
    public List<StrapScreenVO> getStrapScreenListByDeviceId(Long deviceId){
        if (deviceId == null){
            return Collections.emptyList();
        }

        return getScreenVOList(strapScreenMapper.findByDeviceId(deviceId), false);
    }

    /**
     * 转喊成vo列表
     * @param strapScreenEntityList
     * @param needDetail
     * @return
     */
    private List<StrapScreenVO> getScreenVOList(List<StrapScreenEntity> strapScreenEntityList, boolean needDetail){
        if (CollectionUtils.isEmpty(strapScreenEntityList)){
            return Collections.emptyList();
        }

        List<StrapScreenVO> strapScreenVOList = VOUtil.getVOList(StrapScreenVO.class, strapScreenEntityList);

        DeviceInfoEntity deviceInfoEntity = deviceInfoMapper.findById(strapScreenEntityList.get(0).getDeviceId());
        StationInfoEntity stationInfoEntity = null;
        if (deviceInfoEntity != null) {
            stationInfoEntity = stationInfoMapper.findById(deviceInfoEntity.getStationId());
        }
        String stationName = stationInfoEntity == null ? "" : stationInfoEntity.getStationName();
        strapScreenVOList.forEach(strapScreenVO -> {
            //组装设备名称，站点名称，压板屏幕类型，软压板类型
            if (deviceInfoEntity != null) {
                strapScreenVO.setDeviceName(deviceInfoEntity.getDeviceName());
            }
            strapScreenVO.setStationName(stationName);
            if (strapScreenVO.getScreenType() != null) {
                strapScreenVO.setScreenTypeStr(strapCodeMap.get(strapScreenVO.getScreenType()));
            }
            if (strapScreenVO.getSoftType() != null) {
                strapScreenVO.setSoftTypeStr(softCodeMap.get(strapScreenVO.getSoftType()));
            }
            strapScreenVO.setImageUrl();

            //如果需要返回压板详情，再去查询
            if (needDetail){
                strapScreenVO.setStrapDetailVOList(getStrapDetailByScreenId(strapScreenVO.getScreenId()));
            }

        });

        return strapScreenVOList;
    }

    @Override
    public StrapScreenVO getStrapScreenInfo(Long screenId){
        if (screenId == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

        List<StrapScreenEntity> screenEntityList = strapScreenMapper.findByScreenIdIn(Arrays.asList(screenId));
        if (CollectionUtils.isEmpty(screenEntityList)){
            throw new ResultErrorException(ValidationError.setMissing("找不到对应的压板屏: screenId = " + screenId));
        }

        List<StrapScreenVO> strapScreenVOList = getScreenVOList(screenEntityList, true);
        if (CollectionUtils.isEmpty(strapScreenVOList)){
            return VOUtil.getVO(StrapScreenVO.class, screenEntityList.get(0));
        }

        return strapScreenVOList.get(0);

    }

    @Override
    public StrapDetailVO updateStrapDetail(StrapDetailVO strapDetailVO, BaseUserVO currentUser){
        if (strapDetailVO == null || strapDetailVO.getStrapId() == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

        StrapDetailEntity strapDetailEntity = baseMapper.findByStrapId(strapDetailVO.getStrapId());
        if (strapDetailEntity == null){
            throw new ResultErrorException(ValidationError.setMissing("找不到对应的压板: strapId = " + strapDetailVO.getStrapId()));
        }

        strapDetailEntity.setStrapName(strapDetailVO.getStrapName());
        strapDetailEntity.setStrapPosition(strapDetailVO.getStrapPosition());
        strapDetailEntity.setStrapValue(strapDetailVO.getStrapValue());
        strapDetailEntity.setUpdateBy(currentUser.getUserId());
        strapDetailEntity.setUpdateTime(LocalDateTime.now());

        baseMapper.updateById(strapDetailEntity);

        return getStrapDetailVOList(Arrays.asList(strapDetailEntity)).get(0);
    }

    @Override
    public StrapScreenVO updateScreen(StrapScreenVO strapScreenVO, BaseUserVO currentUser){
        if (strapScreenVO == null || strapScreenVO.getScreenId() == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

        List<StrapScreenEntity> strapScreenEntityList = strapScreenMapper.findByScreenIdIn(Arrays.asList(strapScreenVO.getScreenId()));
        if (CollectionUtils.isEmpty(strapScreenEntityList)){
            throw new ResultErrorException(ValidationError.setMissing("找不到对应的压板屏: screenId = " + strapScreenVO.getScreenId()));
        }

        StrapScreenEntity strapScreenEntity = strapScreenEntityList.get(0);
        if (StringUtils.isNotBlank(strapScreenVO.getScreenTypeStr())){
            strapScreenEntity.setScreenType(strapValueMap.get(strapScreenVO.getScreenTypeStr()));
        }

        if (StringUtils.isNotBlank(strapScreenVO.getSoftTypeStr())){
            strapScreenEntity.setSoftType(softValueMap.get(strapScreenVO.getSoftTypeStr()));
        }

        strapScreenEntity.setScreenName(strapScreenVO.getScreenName());
        strapScreenEntity.setSoftPage(strapScreenVO.getSoftPage());
        strapScreenEntity.setRowNumber(strapScreenVO.getRowNumber());
        strapScreenEntity.setColumnNumber(strapScreenVO.getColumnNumber());
        strapScreenEntity.setStrapNumber(strapScreenVO.getStrapNumber());
        strapScreenEntity.setRemark(strapScreenVO.getRemark());
        //附件id不在这里更新
        strapScreenEntity.setUpdateBy(currentUser.getUserId());
        strapScreenEntity.setUpdateTime(LocalDateTime.now());

        strapScreenMapper.updateById(strapScreenEntity);
        return getScreenVOList(Arrays.asList(strapScreenEntity), false).get(0);
    }

    @Override
    public void deleteStrapScreenByDeviceId(Long deviceId, BaseUserVO currentUser){
        if (deviceId == null){
            return;
        }
        deleteStrapScreen(strapScreenMapper.findByDeviceId(deviceId), currentUser);
    }

    /**
     * 逻辑删除压板屏幕 并向下一层删除压板详情
     * @param strapScreenEntityList
     * @param currentUser
     */
    private void deleteStrapScreen(List<StrapScreenEntity> strapScreenEntityList, BaseUserVO currentUser){
        if (CollectionUtils.isEmpty(strapScreenEntityList)){
            return;
        }

        List<StrapDetailEntity> strapDetailEntityList = new ArrayList<>();
        strapScreenEntityList.forEach(strapScreenEntity -> {
            strapScreenEntity.setDel(BaseConstants.BASE_ENTITY_INVALID);
            strapScreenEntity.setUpdateBy(currentUser.getUserId());
            strapScreenEntity.setUpdateTime(LocalDateTime.now());
            strapScreenMapper.updateById(strapScreenEntity);

            strapDetailEntityList.addAll(baseMapper.findByScreenId(strapScreenEntity.getScreenId()));
        });

        deleteStrapDetail(strapDetailEntityList, currentUser);

    }

    /**
     * 逻辑删除压板详情
     * @param strapDetailEntityList
     * @param currentUser
     */
    private void deleteStrapDetail(List<StrapDetailEntity> strapDetailEntityList, BaseUserVO currentUser){
        if (CollectionUtils.isEmpty(strapDetailEntityList)){
            return;
        }

        strapDetailEntityList.forEach(strapDetailEntity -> {
            strapDetailEntity.setDel(BaseConstants.BASE_ENTITY_INVALID);
            strapDetailEntity.setUpdateBy(currentUser.getUserId());
            strapDetailEntity.setUpdateTime(LocalDateTime.now());
        });

        updateBatchById(strapDetailEntityList);
    }

    @Override
    public void deleteScreenById(Long screenId, BaseUserVO currentUser){
        if (screenId == null){
            return;
        }

        deleteStrapScreen(strapScreenMapper.findByScreenIdIn(Arrays.asList(screenId)), currentUser);
    }

    @Override
    public void deleteStrapDetailById(Long strapDetailId, BaseUserVO currentUser){
        if (strapDetailId == null){
            return;
        }
        StrapDetailEntity strapDetailEntity = baseMapper.findByStrapId(strapDetailId);
        if (strapDetailEntity != null){
            deleteStrapDetail(Arrays.asList(strapDetailEntity), currentUser);
        }
    }

    @Override
    public void getImage(Long fkSid, Integer type, HttpServletResponse response){
        attachmentService.downloadImageByTypeAndFkSid(fkSid, type, response);
    }

    @Override
    public void uploadScreenImage(Long screenId, MultipartFile file){
        AttachmentVO attachmentVO = attachmentService.uploadAttachment(file, AttachmentType.SCREEN_IMAGE.getType(), screenId);

        if (attachmentVO != null && attachmentVO.getId() != null){
            updateScreenAttachmentId(screenId, attachmentVO.getId());
        }
    }

    /**
     * 更新压板屏幕图片id
     * @param screenId
     * @param attachmentId
     */
    private void updateScreenAttachmentId(Long screenId, Long attachmentId){
        if (screenId == null){
            log.warn("screenId is null");
            return;
        }
        StrapScreenEntity screenEntity = strapScreenMapper.findByScreenId(screenId);
        Long oldAttachmentId = screenEntity.getAttachmentId();

        //删除之前的文件
        if (oldAttachmentId != null){
            CompletableFuture.runAsync(() -> attachmentService.deleteById(oldAttachmentId), Executors.newSingleThreadExecutor());
        }

        if (screenEntity != null){
            screenEntity.setAttachmentId(attachmentId);
            strapScreenMapper.updateById(screenEntity);
        }
    }

    @Override
    public PageVO<StrapScreenVO> searchScreen(Pageable pageable, ScreenSearchParameter searchParameter){
        Page page = new Page();
        page.setCurrent(pageable.getPageNumber());
        page.setSize(pageable.getPageSize());

        PageVO<StrapScreenVO> strapScreenVOPageVO = new PageVO<>();
        Page<StrapScreenEntity> strapScreenEntityPage = strapScreenMapper.searchScreen(page, searchParameter);
        strapScreenVOPageVO.setPage((int)strapScreenEntityPage.getCurrent());
        strapScreenVOPageVO.setSize((int)strapScreenEntityPage.getSize());
        strapScreenVOPageVO.setTotal(strapScreenEntityPage.getTotal());

        List<StrapScreenEntity> strapScreenEntityList = strapScreenEntityPage.getRecords();

        if (CollectionUtils.isNotEmpty(strapScreenEntityList)){
            strapScreenVOPageVO.setItems(getScreenVOList(strapScreenEntityList, false));
        }

        return strapScreenVOPageVO;
    }

    @Override
    public PageVO<StrapDetailVO> searchStap(Pageable pageable, StrapSearchParameter searchParameter){
        Page page = new Page();
        page.setCurrent(pageable.getPageNumber());
        page.setSize(pageable.getPageSize());

        PageVO<StrapDetailVO> strapDetailVOPageVO = new PageVO<>();
        Page<StrapDetailEntity> strapDetailEntityPage = baseMapper.searchStrap(page, searchParameter);
        strapDetailVOPageVO.setPage((int)strapDetailEntityPage.getCurrent());
        strapDetailVOPageVO.setSize((int)strapDetailEntityPage.getSize());
        strapDetailVOPageVO.setTotal(strapDetailEntityPage.getTotal());

        List<StrapDetailEntity> strapDetailEntityList = strapDetailEntityPage.getRecords();

        if (CollectionUtils.isNotEmpty(strapDetailEntityList)){
            strapDetailVOPageVO.setItems(getStrapDetailVOList(strapDetailEntityList));
        }

        return strapDetailVOPageVO;
    }

}
