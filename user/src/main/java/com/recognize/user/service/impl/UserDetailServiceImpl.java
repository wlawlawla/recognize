package com.recognize.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.recognize.common.common.TranslationalService;
import com.recognize.user.config.UserDetail;
import com.recognize.user.entity.BaseUserEntity;
import com.recognize.user.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@TranslationalService
@Primary
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    public UserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        BaseUserEntity user = userInfoService.getUserEntityByUserName(username);
        // 工具类判断是否查询user对象是否有效
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new UserDetail(user.getUserId(), user.getUserName(), user.getPassword());
    }

}

