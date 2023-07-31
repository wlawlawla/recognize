package com.recognize.device.controller;

import com.recognize.device.service.IDeviceInfoService;
import com.recognize.device.service.IStrapService;
import com.recognize.device.vo.DeviceInfoVO;
import com.recognize.device.vo.StrapDetailVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.util.LoginUser;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private IDeviceInfoService deviceInfoService;

    @Autowired
    private IStrapService strapService;

    /**
     * 导入设备信息
     * @param file
     * @param currentUser
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    public ResponseEntity<List<DeviceInfoVO>> importDevice(@RequestParam(value = "file")MultipartFile file, @LoginUser BaseUserVO currentUser) throws IOException {
        return new ResponseEntity<>(deviceInfoService.importDeviceInfo(file, currentUser), HttpStatus.OK);
    }

    /**
     * 录入设备信息
     * @param deviceInfoVO
     * @param currentUser
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity<DeviceInfoVO> addDevice(@RequestBody DeviceInfoVO deviceInfoVO, @LoginUser BaseUserVO currentUser) throws IOException {
        return new ResponseEntity<>(deviceInfoService.addDevice(deviceInfoVO, currentUser), HttpStatus.OK);
    }

    /**
     * 编辑设备信息
     * @param deviceInfoVO
     * @param currentUser
     * @return
     */
    @PutMapping
    public ResponseEntity<DeviceInfoVO> updateDevice(@RequestBody DeviceInfoVO deviceInfoVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(deviceInfoService.updateDeviceInfo(deviceInfoVO, currentUser), HttpStatus.OK);
    }

    /**
     * 删除设备信息
     * @param deviceId
     * @param currentUser
     */
    @DeleteMapping("/{deviceId}")
    public void deleteDevice(@PathVariable Long deviceId, @LoginUser BaseUserVO currentUser){
        deviceInfoService.deleteDevice(deviceId, currentUser);
    }

    /**
     * 根据设备id获取设备信息 包含压板屏幕列表
     * @param deviceId
     * @return
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceInfoVO> getDeviceDetailById(@PathVariable Long deviceId){
        return new ResponseEntity<>(deviceInfoService.getDeviceInfoById(deviceId), HttpStatus.OK);
    }

    /**
     * 新增压板屏幕
     * @param strapScreenVO
     * @param currentUser
     * @return
     */
    @PostMapping("/screen")
    public ResponseEntity<StrapScreenVO> addStrapScreenInfo(@RequestBody StrapScreenVO strapScreenVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.addStrapScreen(strapScreenVO, currentUser), HttpStatus.OK);
    }

    /**
     * 获取压板屏信息 包含压板列表
     * @param screenId
     * @return
     */
    @GetMapping("/screen/{screenId}")
    public ResponseEntity<StrapScreenVO> getStrapScreenInfoById(@PathVariable Long screenId){
        return new ResponseEntity<>(strapService.getStrapScreenInfo(screenId), HttpStatus.OK);
    }

    /**
     * 编辑压板屏 不包含图片
     * @param strapScreenVO
     * @param currentUser
     * @return
     */
    @PutMapping("/screen")
    public ResponseEntity<StrapScreenVO> updateScreenInfo(@RequestBody StrapScreenVO strapScreenVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.updateScreen(strapScreenVO, currentUser), HttpStatus.OK);
    }

    /**
     * 逻辑删除压板屏幕
     * @param screenId
     * @param currentUser
     */
    @DeleteMapping("/screen/{screenId}")
    public void deleteScreenById(@PathVariable Long screenId, @LoginUser BaseUserVO currentUser){
        strapService.deleteScreenById(screenId, currentUser);
    }

    /**
     * 添加压板信息
     * @param strapDetailVO
     * @param currentUser
     * @return
     */
    @PostMapping("/screen/strap")
    public ResponseEntity<StrapDetailVO> addStrapInfo(@RequestBody StrapDetailVO strapDetailVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.updateStrapDetail(strapDetailVO, currentUser), HttpStatus.OK);
    }

    /**
     * 编辑压板信息
     * @param strapDetailVO
     * @param currentUser
     * @return
     */
    @PutMapping("/screen/strap")
    public ResponseEntity<StrapDetailVO> updateStrapInfo(@RequestBody StrapDetailVO strapDetailVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(strapService.updateStrapDetail(strapDetailVO, currentUser), HttpStatus.OK);
    }

    /**
     * 删除压板
     * @param strapId
     * @param currentUser
     */
    @DeleteMapping("/screen/strap/{strapId}")
    public void deleteByStrapId(@PathVariable Long strapId, @LoginUser BaseUserVO currentUser){
        strapService.deleteStrapDetailById(strapId, currentUser);
    }
}
