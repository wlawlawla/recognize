package com.recognize.user.controller;

import com.recognize.common.common.PrivilegeConstants;
import com.recognize.common.vo.PageVO;
import com.recognize.user.dao.BaseUserDto;
import com.recognize.user.parameter.UserSearchParameter;
import com.recognize.user.service.IUserInfoService;
import com.recognize.user.util.LoginUser;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class BaseUserController {

    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 新增用户
     * @param baseUserDto
     * @param baseUserVO
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.USER_ADD + "')")
    public ResponseEntity<BaseUserVO> addUser(@Valid @RequestBody BaseUserDto baseUserDto, @LoginUser BaseUserVO baseUserVO){
        return new ResponseEntity<>(userInfoService.addUser(baseUserDto, baseUserVO), HttpStatus.OK);
    }

    /**
     * 编辑用户
     * @param baseUserDto
     * @param baseUserVO
     * @return
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.USER_UPDATE + "')")
    public ResponseEntity<BaseUserVO> updateUser(@Valid @RequestBody BaseUserDto baseUserDto, @LoginUser BaseUserVO baseUserVO){
        return new ResponseEntity<>(userInfoService.updateUser(baseUserDto, baseUserVO), HttpStatus.OK);
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.USER_DELETE + "')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        return new ResponseEntity<>(userInfoService.deleteByUserId(userId), HttpStatus.OK);
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.USER_VIEW + "')")
    public ResponseEntity<BaseUserVO> getUserInfo(@PathVariable Long userId){
        return new ResponseEntity<>(userInfoService.getUserInfoByUserId(userId), HttpStatus.OK);
    }

    /**
     * 搜索用户 分页
     * @param pageable
     * @param userSearchParameter
     * @return
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.USER_VIEW + "')")
    public ResponseEntity<PageVO<BaseUserVO>> searchUser(@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestBody UserSearchParameter userSearchParameter){
        return new ResponseEntity<>(userInfoService.searchUserPage(pageable, userSearchParameter), HttpStatus.OK);
    }

}
