package com.recognize.user.service;

import com.recognize.common.vo.PageVO;
import com.recognize.user.dao.BaseUserDto;
import com.recognize.user.entity.BaseRoleEntity;
import com.recognize.user.entity.BaseUserEntity;
import com.recognize.user.parameter.UserSearchParameter;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserInfoService {

    /**
     * 获取角色
     * @param userId
     * @return
     */
    List<BaseRoleEntity> getRolesByUserId(Long userId);

    /**
     * 获取权限
     * @param userId
     * @return
     */
    List<String> getAuthPriByUserId(Long userId);

    /**
     * 新增用户
     * @param baseUserDto
     * @param currentUser
     * @return
     */
    BaseUserVO addUser(BaseUserDto baseUserDto, BaseUserVO currentUser);

    /**
     * 查询用户
     * @param userName
     * @return
     */
    BaseUserEntity getUserEntityByUserName(String userName);

    /**
     * 删除用户
     * @param userId
     * @return userName
     */
    String deleteByUserId(Long userId);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    BaseUserVO getUserInfoByUserId(Long userId);

    /**
     * 分页搜索用户列表
     * @param pageable
     * @param userSearchParameter
     * @return
     */
    PageVO<BaseUserVO> searchUserPage(Pageable pageable, UserSearchParameter userSearchParameter);

    /**
     * 编辑用户信息
     * @param baseUserDto
     * @param currentUser
     * @return
     */
    BaseUserVO updateUser(BaseUserDto baseUserDto, BaseUserVO currentUser);

    /**
     * 获取用户基本信息
     * @param userIds
     * @return
     */
    List<BaseUserVO> getBaseUserVOByUserIdIn(List<Long> userIds);

    /**
     * 根据角色id查询用户列表
     * @param roleId
     * @return
     */
    List<BaseUserVO> getBaseUserList(Long roleId);
}
