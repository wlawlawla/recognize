package com.recognize.user.config;

import com.recognize.common.util.SpringUtil;
import com.recognize.common.util.VOUtil;
import com.recognize.user.entity.BaseUserEntity;
import com.recognize.user.service.IUserInfoService;
import com.recognize.user.service.impl.UserInfoServiceImpl;
import com.recognize.user.vo.BaseUserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JWTAuthenticationTokenFilter extends BasicAuthenticationFilter {

    private IUserInfoService userInfoService;

    public JWTAuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中JWT的Token
        String tokenHeader = request.getHeader(JWTConfig.tokenHeader);
        if (null!=tokenHeader && tokenHeader.startsWith(JWTConfig.tokenPrefix)) {
            try {
                // 截取JWT前缀
                String token = tokenHeader.replace(JWTConfig.tokenPrefix, "");
                // 解析JWT
                Claims claims = Jwts.parser()
                        .setSigningKey(JWTConfig.secret)
                        .parseClaimsJws(token)
                        .getBody();
                // 获取用户名
                String username = claims.getSubject();
                String userId=claims.getId();

                if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(userId)) {
                    BaseUserEntity baseUserEntity = new BaseUserEntity();
                    baseUserEntity.setUserName(claims.getSubject());
                    baseUserEntity.setUserId(Long.parseLong(claims.getId()));
                    List<String> userAuthPrivileges = getUserInfoService().getAuthPriByUserId(Long.valueOf(userId));
                    baseUserEntity.setAuthorities(userAuthPrivileges.stream().map(privilege -> new SimpleGrantedAuthority(privilege)).collect(Collectors.toList()));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(VOUtil.getVO(BaseUserVO.class, baseUserEntity), userId, baseUserEntity.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e){
                log.info("Token过期");
            } catch (Exception e) {
                log.info("Token无效");
            }
        }
        filterChain.doFilter(request, response);
        return;
    }

    private IUserInfoService getUserInfoService(){
        if (userInfoService == null){
            userInfoService = SpringUtil.getBean(UserInfoServiceImpl.class);
        }
        return userInfoService;
    }
}

