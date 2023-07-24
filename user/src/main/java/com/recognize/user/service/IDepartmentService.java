package com.recognize.user.service;

import com.recognize.user.vo.BaseDepartmentVO;
import com.recognize.user.vo.BaseUserVO;

import java.util.List;

public interface IDepartmentService {

    /**
     * 获取部门树
     * @return
     */
    List<BaseDepartmentVO> getDepartmentTree();

    /**
     * 添加部门
     * @param baseDepartmentVO
     * @param currentUser
     * @return
     */
    BaseDepartmentVO addDepartment(BaseDepartmentVO baseDepartmentVO, BaseUserVO currentUser);

    /**
     * 删除部门 逻辑删除
     * @param departmentId
     * @param currentUser
     */
    String deleteDepartment(Long departmentId, BaseUserVO currentUser);

    /**
     * 更新部门信息
     * @param baseDepartmentVO
     * @param currentUser
     * @return
     */
    BaseDepartmentVO updateDepartment(BaseDepartmentVO baseDepartmentVO, BaseUserVO currentUser);

}
