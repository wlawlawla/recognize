package com.recognize.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.recognize.common.common.TranslationalService;
import com.recognize.user.entity.BaseUserRoleXwEntity;
import com.recognize.user.mapper.BaseUserRoleXwMapper;
import com.recognize.user.service.IUserRoleXwService;
import com.recognize.user.vo.BaseUserVO;

import java.util.ArrayList;
import java.util.List;

@TranslationalService
public class UserRoleXwServiceImpl extends ServiceImpl<BaseUserRoleXwMapper, BaseUserRoleXwEntity> implements IUserRoleXwService {

    @Override
    public List<BaseUserRoleXwEntity> saveUserRoleXwBatch(Long userId, List<Long> roleIdList, Long currentUserId){

        List<BaseUserRoleXwEntity> baseUserRoleXwEntityList = new ArrayList<>();

        if (userId != null && CollectionUtils.isNotEmpty(roleIdList)){
            roleIdList.forEach(roleId -> baseUserRoleXwEntityList.add(new BaseUserRoleXwEntity(userId, roleId, currentUserId)));
        }

        if (CollectionUtils.isNotEmpty(baseUserRoleXwEntityList)){
            saveBatch(baseUserRoleXwEntityList);
        }
        return baseUserRoleXwEntityList;
    }

    @Override
    public void deleteUserRoleXwByUserId(Long userId, BaseUserVO currentUser){
        if (userId != null){
            baseMapper.updateUserRoleXwByUserId(userId, currentUser.getUserId());
        }
    }
}
