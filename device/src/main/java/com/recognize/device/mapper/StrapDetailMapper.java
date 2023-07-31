package com.recognize.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.device.entity.StrapDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrapDetailMapper extends BaseMapper<StrapDetailEntity> {

    /**
     * 根据压板屏id查询
     * @param screenId
     * @return
     */
    List<StrapDetailEntity> findByScreenId(@Param("screenId") Long screenId);

    /**
     * 查询压板信息
     * @param strapId
     * @return
     */
    StrapDetailEntity findByStrapId(@Param("strapId") Long strapId);

}
