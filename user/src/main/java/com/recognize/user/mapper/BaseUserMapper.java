package com.recognize.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.user.entity.BaseUserEntity;
import com.recognize.user.parameter.UserSearchParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseUserMapper extends BaseMapper<BaseUserEntity> {

    /**
     * 获取所有用户信息
     * @return
     */
    List<BaseUserEntity> findAll();

    /**
     * 查询用户 by id
     * @param id
     * @return
     */
    BaseUserEntity findById(@Param("id") Long id);

    /**
     * 查询用户 by name
     * @param name
     * @return
     */
    BaseUserEntity findByName(@Param("name") String name);

    /**
     * 用户列表搜索
     * @param page
     * @param param
     * @return
     */
    Page<BaseUserEntity> searchUser(Page page, @Param("param") UserSearchParameter param);

    /**
     * 批量查询用户
     * @param ids
     * @return
     */
    List<BaseUserEntity> findByIdIn(@Param("ids") List<Long> ids);


}
