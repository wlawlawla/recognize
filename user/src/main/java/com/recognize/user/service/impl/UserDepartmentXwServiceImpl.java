package com.recognize.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.recognize.common.common.TranslationalService;
import com.recognize.user.entity.BaseUserDepartmentXwEntity;
import com.recognize.user.mapper.BaseUserDepartmentXwMapper;
import com.recognize.user.service.IUserDepartmentXwService;
import com.recognize.user.vo.BaseUserVO;

import java.util.ArrayList;
import java.util.List;

@TranslationalService
public class UserDepartmentXwServiceImpl extends ServiceImpl<BaseUserDepartmentXwMapper, BaseUserDepartmentXwEntity> implements IUserDepartmentXwService {

    @Override
    public List<BaseUserDepartmentXwEntity> saveUserDepartmentXwBatch(Long userId, List<Long> departmentIdList, Long currentUserId){

        List<BaseUserDepartmentXwEntity> baseUserDepartmentXwEntityList = new ArrayList<>();

        if (userId != null && CollectionUtils.isNotEmpty(departmentIdList)){
            departmentIdList.forEach(departmentId -> baseUserDepartmentXwEntityList.add(new BaseUserDepartmentXwEntity(userId, departmentId, currentUserId)));
        }

        if (CollectionUtils.isNotEmpty(baseUserDepartmentXwEntityList)){
            saveBatch(baseUserDepartmentXwEntityList);
        }
        return baseUserDepartmentXwEntityList;
    }

    @Override
    public void deleteUserDepartmentXwByUserId(Long userId, BaseUserVO currentUser){
        if (userId != null){
            baseMapper.updateUserDepartmentXwByUserId(userId, currentUser.getUserId());
        }
    }
}
