package com.recognize.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.user.entity.BasePrivilegeEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasePrivilegeMapper extends BaseMapper<BasePrivilegeEntity> {

    List<BasePrivilegeEntity> findByUserId(@Param("userId") Long userId);
}