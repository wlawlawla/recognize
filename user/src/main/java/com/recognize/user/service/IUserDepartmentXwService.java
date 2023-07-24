package com.recognize.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.recognize.user.entity.BaseUserDepartmentXwEntity;
import com.recognize.user.vo.BaseUserVO;

import java.util.List;

public interface IUserDepartmentXwService extends IService<BaseUserDepartmentXwEntity> {

    /**
     * 保存用户-部门映射
     * @param userId
     * @param departmentIdList
     * @param currentUserId
     * @return
     */
    List<BaseUserDepartmentXwEntity> saveUserDepartmentXwBatch(Long userId, List<Long> departmentIdList, Long currentUserId);

    /**
     * 删除角色部门关联
     * 逻辑删除
     * @param userId
     * @param currentUser
     */
    void deleteUserDepartmentXwByUserId(Long userId, BaseUserVO currentUser);

}
