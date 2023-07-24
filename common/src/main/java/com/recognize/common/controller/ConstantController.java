package com.recognize.common.controller;

import com.recognize.common.service.IConstantService;
import com.recognize.common.vo.ConstantVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/constant")
public class ConstantController {

    @Autowired
    private IConstantService constantService;

    /**
     * 获取对应常量列表
     * @param type
     * @return
     */
    @GetMapping
    public ResponseEntity<List<ConstantVO>> getConstantByType(@RequestParam(value = "type") String type){
        return new ResponseEntity<>(constantService.getConstantByType(type), HttpStatus.OK);
    }

    /**
     * 刷新常量缓存
     */
    @GetMapping("/refresh")
    public void refreshConstant(){
        constantService.refresh();
    }
}
