package com.recognize.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.device.entity.StrapScreenEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrapScreenMapper extends BaseMapper<StrapScreenEntity> {

    /**
     * 批量查询压板屏幕
     * @param screenIdList
     * @return
     */
    List<StrapScreenEntity> findByScreenIdIn(@Param("screenIdList") List<Long> screenIdList);

    /**
     * 根据设备id查询压板屏幕信息
     * @param deviceId
     * @return
     */
    List<StrapScreenEntity> findByDeviceId(@Param("deviceId") Long deviceId);

}
