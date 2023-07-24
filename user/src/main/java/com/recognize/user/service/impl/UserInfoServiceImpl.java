package com.recognize.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.exception.ResultErrorEnum;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.exception.ValidationError;
import com.recognize.common.util.VOUtil;
import com.recognize.common.vo.PageVO;
import com.recognize.user.dao.BaseUserDto;
import com.recognize.user.entity.BasePrivilegeEntity;
import com.recognize.user.entity.BaseRoleEntity;
import com.recognize.user.entity.BaseUserEntity;
import com.recognize.user.mapper.*;
import com.recognize.user.parameter.UserSearchParameter;
import com.recognize.user.service.IUserDepartmentXwService;
import com.recognize.user.service.IUserInfoService;
import com.recognize.user.service.IUserRoleXwService;
import com.recognize.user.vo.BaseUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@TranslationalService
@Primary
@Slf4j
public class UserInfoServiceImpl implements IUserInfoService {

    @Autowired
    private BaseUserMapper baseUserMapper;

    @Autowired
    private BaseRoleMapper baseRoleMapper;

    @Autowired
    private BasePrivilegeMapper basePrivilegeMapper;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private IUserDepartmentXwService userDepartmentXwService;

    @Autowired
    private IUserRoleXwService userRoleXwService;


    @Override
    public List<BaseRoleEntity> getRolesByUserId(Long userId) {
        if (userId == null){
            return Collections.EMPTY_LIST;
        }

        List<BaseRoleEntity> roles = baseRoleMapper.findByUserId(userId);
        return roles;
    }

    @Override
    public List<String> getAuthPriByUserId(Long userId){
        if (userId != null){
            List<BasePrivilegeEntity> authPrivilegeEntities = basePrivilegeMapper.findByUserId(userId);
            return authPrivilegeEntities.stream().map(BasePrivilegeEntity::getPrivilegeCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public BaseUserVO addUser(BaseUserDto baseUserDto, BaseUserVO currentUser){
        BaseUserEntity baseUserEntity = VOUtil.getVO(BaseUserEntity.class, baseUserDto);
        if (baseUserEntity == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);

        }
        //密码转换
        String password = encoder.encode(baseUserDto.getPassword());

        baseUserEntity.setPassword(password);

        baseUserEntity.setCreateBy(currentUser.getUserId());
        baseUserEntity.setUpdateBy(currentUser.getUserId());

        //用户信息保存
        baseUserMapper.insert(baseUserEntity);

        //用户部门信息保存
        userDepartmentXwService.saveUserDepartmentXwBatch(baseUserEntity.getUserId(), baseUserDto.getDepartmentIds(), currentUser.getUserId());

        //用户角色信息保存
        userRoleXwService.saveUserRoleXwBatch(baseUserEntity.getUserId(), baseUserDto.getRoleIds(), currentUser.getUserId());

        BaseUserVO baseUserVO = VOUtil.getVO(BaseUserVO.class, baseUserEntity);

        //获取用户权限code
        baseUserVO.setAuthorities(getAuthPriByUserId(baseUserEntity.getUserId()));

        return baseUserVO;
    }

    @Override
    public BaseUserVO updateUser(BaseUserDto baseUserDto, BaseUserVO currentUser){
        BaseUserEntity baseUserEntity = VOUtil.getVO(BaseUserEntity.class, baseUserDto);
        if (baseUserEntity == null || baseUserEntity.getUserId() == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

        BaseUserEntity dbUserEntity = baseUserMapper.findById(baseUserEntity.getUserId());
        if (dbUserEntity == null){
            throw new ResultErrorException(ValidationError.setMissing("user can not find : userId = " + baseUserEntity.getUserId()));
        }

        //todo 密码暂时不可更改 等需求明确之后看是否要提供独立接口
        baseUserEntity.setPassword(dbUserEntity.getPassword());


/*        //密码转换
        String password = encoder.encode(baseUserDto.getPassword());

        baseUserEntity.setPassword(password);*/
        baseUserEntity.setUpdateBy(currentUser.getUserId());
        baseUserEntity.setUpdateTime(LocalDateTime.now());

        //用户信息保存
        baseUserMapper.updateById(baseUserEntity);

        //逻辑删除用户的角色、部门映射，重新保存  todo 待确认需求 目前可编辑部门和角色关联
        userDepartmentXwService.deleteUserDepartmentXwByUserId(baseUserEntity.getUserId(), currentUser);
        userRoleXwService.deleteUserRoleXwByUserId(baseUserEntity.getUserId(), currentUser);

        //用户部门信息保存
        userDepartmentXwService.saveUserDepartmentXwBatch(baseUserEntity.getUserId(), baseUserDto.getDepartmentIds(), currentUser.getUserId());

        //用户角色信息保存
        userRoleXwService.saveUserRoleXwBatch(baseUserEntity.getUserId(), baseUserDto.getRoleIds(), currentUser.getUserId());

        BaseUserVO baseUserVO = VOUtil.getVO(BaseUserVO.class, baseUserEntity);

        //获取用户权限code
        baseUserVO.setAuthorities(getAuthPriByUserId(baseUserEntity.getUserId()));

        return baseUserVO;
    }

    @Override
    public BaseUserEntity getUserEntityByUserName(String userName){
        if (StringUtils.isBlank(userName)){
            return null;
        }
        return baseUserMapper.findByName(userName);
    }

    @Override
    public String deleteByUserId(Long userId){
        try {
            String userName = baseUserMapper.selectById(userId).getUserName();
            baseUserMapper.deleteById(userId);
            return userName;
        } catch (RuntimeException e){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

    }


    @Override
    public BaseUserVO getUserInfoByUserId(Long userId){
        if (userId == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }
        BaseUserEntity baseUserEntity = baseUserMapper.findById(userId);
        if (baseUserEntity == null){
            throw new ResultErrorException(ValidationError.setMissing("user not found : user_id = " + userId));
        }

        BaseUserVO baseUserVO = VOUtil.getVO(BaseUserVO.class, baseUserEntity);
        baseUserVO.setAuthorities(getAuthPriByUserId(userId));

        return baseUserVO;

    }

    @Override
    public PageVO<BaseUserVO> searchUserPage(Pageable pageable, UserSearchParameter userSearchParameter){
        Page page = new Page();
        page.setCurrent(pageable.getPageNumber());
        page.setSize(pageable.getPageSize());
        Page<BaseUserEntity> userEntityIPage = baseUserMapper.searchUser(page, userSearchParameter);

        PageVO<BaseUserVO> baseUserVOPageVO = new PageVO<>();
        baseUserVOPageVO.setPage((int)userEntityIPage.getCurrent());
        baseUserVOPageVO.setSize((int)userEntityIPage.getSize());
        baseUserVOPageVO.setTotal(userEntityIPage.getTotal());

        List<BaseUserEntity> baseUserEntityList = userEntityIPage.getRecords();
        if (CollectionUtils.isNotEmpty(baseUserEntityList)){
            baseUserVOPageVO.setItems(VOUtil.getVOList(BaseUserVO.class, baseUserEntityList));
        }

        return baseUserVOPageVO;
    }


}

