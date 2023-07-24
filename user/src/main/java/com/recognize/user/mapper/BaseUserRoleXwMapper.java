package com.recognize.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.user.entity.BaseUserRoleXwEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseUserRoleXwMapper extends BaseMapper<BaseUserRoleXwEntity> {


    List<BaseUserRoleXwEntity> findByRoleId(@Param("roleId") Long roleId);

    List<BaseUserRoleXwEntity> findByUserId(@Param("userId") Long userId);

    /**
     * 逻辑删除用户角色关联信息
     * @param userId
     * @param updateUserId
     */
    void updateUserRoleXwByUserId(@Param("userId") Long userId, @Param("updateUserId") Long updateUserId);

}
