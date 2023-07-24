package com.recognize.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.recognize.user.entity.BaseUserDepartmentXwEntity;
import com.recognize.user.entity.BaseUserRoleXwEntity;
import com.recognize.user.vo.BaseUserVO;

import java.util.List;

public interface IUserRoleXwService extends IService<BaseUserRoleXwEntity> {

    /**
     * 保存用户-角色映射
     * @param userId
     * @param roleIdList
     * @param currentUserId
     * @return
     */
    List<BaseUserRoleXwEntity> saveUserRoleXwBatch(Long userId, List<Long> roleIdList, Long currentUserId);

    /**
     * 删除用户角色关联
     * 逻辑删除
     * @param userId
     * @param currentUser
     */
    void deleteUserRoleXwByUserId(Long userId, BaseUserVO currentUser);
}
