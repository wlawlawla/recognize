package com.recognize.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.common.entity.BaseConstantEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseConstantMapper extends BaseMapper<BaseConstantEntity> {

    List<BaseConstantEntity> findAll();

}
