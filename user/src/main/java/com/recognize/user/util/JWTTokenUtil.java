package com.recognize.user.util;

import com.alibaba.fastjson.JSON;
import com.recognize.user.config.JWTConfig;
import com.recognize.user.vo.BaseUserVO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JWTTokenUtil {

    /**
     * 私有化构造器
     */
    private JWTTokenUtil(){}

    /**
     * 生成Token
     */
    public static String createAccessToken(BaseUserVO baseUserVO){
        // 登陆成功生成JWT
        String token = Jwts.builder()
                // 放入用户名和用户ID
                .setId(baseUserVO.getUserId()+"")
                // 主题
                .setSubject(baseUserVO.getUserName())
                // 签发时间
                .setIssuedAt(new Date())
                // 签发者
                .setIssuer("andy")
                // 自定义属性 放入用户拥有权限
                .claim("authorities", JSON.toJSONString(baseUserVO.getAuthorities()))
                // 失效时间
                //.setExpiration(new Date(System.currentTimeMillis() + JWTConfig.expiration))
                // 签名算法和密钥
                .signWith(SignatureAlgorithm.HS512, JWTConfig.secret)
                .compact();
        return token;
    }
}
