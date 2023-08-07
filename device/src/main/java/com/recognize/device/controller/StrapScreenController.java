package com.recognize.device.controller;

import com.recognize.common.common.PrivilegeConstants;
import com.recognize.common.vo.PageVO;
import com.recognize.device.parameter.ScreenSearchParameter;
import com.recognize.device.parameter.StrapSearchParameter;
import com.recognize.device.service.IStrapService;
import com.recognize.device.vo.StrapDetailVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.util.LoginUser;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/device/screen")
public class StrapScreenController {

    @Autowired
    private IStrapService strapService;

    /**
     * 新增压板屏幕
     * @param strapScreenVO
     * @param currentUser
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.SCREEN_ADD + "')")
    public ResponseEntity<StrapScreenVO> addStrapScreenInfo(@RequestBody StrapScreenVO strapScreenVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.addStrapScreen(strapScreenVO, currentUser), HttpStatus.OK);
    }

    /**
     * 获取压板屏信息 包含压板列表
     * @param screenId
     * @return
     */
    @GetMapping("/{screenId}")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.SCREEN_VIEW + "')")
    public ResponseEntity<StrapScreenVO> getStrapScreenInfoById(@PathVariable Long screenId){
        return new ResponseEntity<>(strapService.getStrapScreenInfo(screenId), HttpStatus.OK);
    }

    /**
     * 编辑压板屏 不包含图片
     * @param strapScreenVO
     * @param currentUser
     * @return
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.SCREEN_UPDATE + "')")
    public ResponseEntity<StrapScreenVO> updateScreenInfo(@RequestBody StrapScreenVO strapScreenVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.updateScreen(strapScreenVO, currentUser), HttpStatus.OK);
    }

    /**
     * 逻辑删除压板屏幕
     * @param screenId
     * @param currentUser
     */
    @DeleteMapping("/{screenId}")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.SCREEN_DELETE + "')")
    public void deleteScreenById(@PathVariable Long screenId, @LoginUser BaseUserVO currentUser){
        strapService.deleteScreenById(screenId, currentUser);
    }


    /**
     * 添加压板信息
     * @param strapDetailVO
     * @param currentUser
     * @return
     */
    @PostMapping("/strap")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.STRAP_ADD + "')")
    public ResponseEntity<StrapDetailVO> addStrapInfo(@RequestBody StrapDetailVO strapDetailVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.updateStrapDetail(strapDetailVO, currentUser), HttpStatus.OK);
    }

    /**
     * 编辑压板信息
     * @param strapDetailVO
     * @param currentUser
     * @return
     */
    @PutMapping("/strap")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.SCREEN_UPDATE + "')")
    public ResponseEntity<StrapDetailVO> updateStrapInfo(@RequestBody StrapDetailVO strapDetailVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.updateStrapDetail(strapDetailVO, currentUser), HttpStatus.OK);
    }

    /**
     * 删除压板
     * @param strapId
     * @param currentUser
     */
    @DeleteMapping("/strap/{strapId}")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.SCREEN_DELETE + "')")
    public void deleteByStrapId(@PathVariable Long strapId, @LoginUser BaseUserVO currentUser){
        strapService.deleteStrapDetailById(strapId, currentUser);
    }

    /**
     * 上传压板屏幕图片
     * @param file
     * @param screenId
     */
    @PostMapping("/image")
    public void uploadScreenImage(@RequestBody MultipartFile file, @RequestParam("screenId") Long screenId){
        strapService.uploadScreenImage(screenId, file);
    }

    /**
     * 分页查询压板屏幕
     * @param pageable
     * @param searchParameter
     * @return
     */
    @PostMapping("/search")
    public ResponseEntity<PageVO<StrapScreenVO>> searchScreen(@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestBody ScreenSearchParameter searchParameter){
        return new ResponseEntity<>(strapService.searchScreen(pageable, searchParameter), HttpStatus.OK);
    }

    /**
     * 搜索压板
     * @param pageable
     * @param searchParameter
     * @return
     */
    @PostMapping("/strap/search")
    public ResponseEntity<PageVO<StrapDetailVO>> searchStrap(@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestBody StrapSearchParameter searchParameter){
        return new ResponseEntity<>(strapService.searchStap(pageable, searchParameter), HttpStatus.OK);
    }

}
