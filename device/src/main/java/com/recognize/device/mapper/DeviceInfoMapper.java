package com.recognize.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.device.entity.DeviceInfoEntity;
import com.recognize.device.parameter.DeviceSearchParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceInfoMapper extends BaseMapper<DeviceInfoEntity> {

    /**
     * 批量查询
     * @param deviceIdList
     * @return
     */
    List<DeviceInfoEntity> findByDeviceIdIn(@Param("deviceIdList") List<Long> deviceIdList);

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    DeviceInfoEntity findById(@Param("id") Long id);

    /**
     * 设备列表
     * @param param
     * @return
     */
    Page<DeviceInfoEntity> searchDevice(Page page, @Param("param")DeviceSearchParameter param);

}
