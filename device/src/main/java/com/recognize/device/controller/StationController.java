package com.recognize.device.controller;

import com.recognize.device.service.IDeviceInfoService;
import com.recognize.device.vo.StationInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {

    @Autowired
    private IDeviceInfoService deviceInfoService;

    /**
     * 获取站点列表
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<List<StationInfoVO>> getAllStation(){
        return new ResponseEntity<>(deviceInfoService.getStationList(), HttpStatus.OK);
    }
}
