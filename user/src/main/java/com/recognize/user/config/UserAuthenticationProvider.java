package com.recognize.user.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.common.util.VOUtil;
import com.recognize.user.entity.BaseUserEntity;
import com.recognize.user.service.IUserInfoService;
import com.recognize.user.service.impl.UserDetailServiceImpl;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取表单输入中返回的用户名
        String userName = (String) authentication.getPrincipal();
        // 获取表单中输入的密码
        String password = (String) authentication.getCredentials();
        // 查询用户是否存在
        BaseUserEntity userInfo = userInfoService.getUserEntityByUserName(userName);
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 我们还要判断密码是否正确，这里我们的密码使用BCryptPasswordEncoder进行加密的
        if (!encoder.matches(password, userInfo.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }
        // 还可以加一些其他信息的判断，比如用户账号已停用等判断
        // 角色集合
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 查询用户角色
        List<String> userPrivileges = userInfoService.getAuthPriByUserId(userInfo.getUserId());

        if (CollectionUtils.isNotEmpty(userPrivileges)){
            authorities.addAll(userPrivileges.stream().map(privilege -> new SimpleGrantedAuthority(privilege)).collect(Collectors.toList()));
        }

        userInfo.setAuthorities(authorities);
        // 进行登录
        return new UsernamePasswordAuthenticationToken(VOUtil.getVO(BaseUserVO.class, userInfo), password, authorities);
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
