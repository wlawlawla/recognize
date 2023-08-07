package com.recognize.device.controller;

import com.recognize.common.common.PrivilegeConstants;
import com.recognize.common.vo.PageVO;
import com.recognize.device.parameter.DeviceSearchParameter;
import com.recognize.device.service.IDeviceInfoService;
import com.recognize.device.service.IStrapService;
import com.recognize.device.vo.DeviceInfoVO;
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

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private IDeviceInfoService deviceInfoService;

    /**
     * 导入设备信息
     * @param file
     * @param currentUser
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.DEVICE_ADD + "')")
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
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.DEVICE_ADD + "')")
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
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.DEVICE_UPDATE + "')")
    public ResponseEntity<DeviceInfoVO> updateDevice(@RequestBody DeviceInfoVO deviceInfoVO, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(deviceInfoService.updateDeviceInfo(deviceInfoVO, currentUser), HttpStatus.OK);
    }

    /**
     * 删除设备信息
     * @param deviceId
     * @param currentUser
     */
    @DeleteMapping("/{deviceId}")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.DEVICE_DELETE + "')")
    public void deleteDevice(@PathVariable Long deviceId, @LoginUser BaseUserVO currentUser){
        deviceInfoService.deleteDevice(deviceId, currentUser);
    }

    /**
     * 根据设备id获取设备信息 包含压板屏幕列表
     * @param deviceId
     * @return
     */
    @GetMapping("/{deviceId}")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.DEVICE_VIEW + "')")
    public ResponseEntity<DeviceInfoVO> getDeviceDetailById(@PathVariable Long deviceId){
        return new ResponseEntity<>(deviceInfoService.getDeviceInfoById(deviceId), HttpStatus.OK);
    }

    /**
     * 搜索设备列表
     * @param pageable
     * @param deviceSearchParameter
     * @return
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('" + PrivilegeConstants.DEVICE_VIEW + "')")
    public ResponseEntity<PageVO<DeviceInfoVO>> searchDevice(@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestBody DeviceSearchParameter deviceSearchParameter){
        return new ResponseEntity<>(deviceInfoService.searchDevice(pageable, deviceSearchParameter), HttpStatus.OK);
    }

}
