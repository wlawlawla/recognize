package com.recognize.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.user.entity.BaseRoleEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseRoleMapper extends BaseMapper<BaseRoleEntity> {

    List<BaseRoleEntity> findAll();

    BaseRoleEntity findByRoleId(@Param("roleId") Long roleId);

    List<BaseRoleEntity> findByUserId(@Param("userId") Long UserId);

}
