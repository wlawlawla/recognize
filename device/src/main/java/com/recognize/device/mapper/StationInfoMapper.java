package com.recognize.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.device.entity.StationInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationInfoMapper extends BaseMapper<StationInfoEntity> {

    /**
     * 批量查询
     * @param stationIdList
     * @return
     */
    List<StationInfoEntity> findByStationIdIn(@Param("stationIdList") List<Long> stationIdList);

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    StationInfoEntity findById(@Param("id") Long id);

    /**
     * 根据名称查询
     * @param stationName
     * @return
     */
    StationInfoEntity findByStationName(@Param("stationName") String stationName);

    /**
     * 获取所有站点
     * @return
     */
    List<StationInfoEntity> findAll();
}
