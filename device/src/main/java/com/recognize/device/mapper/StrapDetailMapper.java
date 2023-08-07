package com.recognize.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.device.entity.StrapDetailEntity;
import com.recognize.device.parameter.StrapSearchParameter;
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

    /**
     * 搜索压板
     * @param page
     * @param parameter
     * @return
     */
    Page<StrapDetailEntity> searchStrap(Page page, @Param("param")StrapSearchParameter parameter);

}
