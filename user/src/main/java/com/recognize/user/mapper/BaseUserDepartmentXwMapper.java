package com.recognize.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.user.entity.BaseUserDepartmentXwEntity;
import com.recognize.user.entity.BaseUserRoleXwEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseUserDepartmentXwMapper extends BaseMapper<BaseUserDepartmentXwEntity> {

    /**
     * 逻辑删除用户部门关联信息
     * @param userId
     * @param updateUserId
     */
    void updateUserDepartmentXwByUserId(@Param("userId") Long userId, @Param("updateUserId") Long updateUserId);
}
