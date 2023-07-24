package com.recognize.user.controller;

import com.recognize.user.service.IDepartmentService;
import com.recognize.user.util.LoginUser;
import com.recognize.user.vo.BaseDepartmentVO;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/department")
public class BaseDepartmentController {

    @Autowired
    private IDepartmentService departmentService;

    /**
     * 新增部门
     * @param baseDepartmentVO
     * @param baseUserVO
     * @return
     */
    @PostMapping
    public ResponseEntity<BaseDepartmentVO> addDepartment(@Valid @RequestBody BaseDepartmentVO baseDepartmentVO, @LoginUser BaseUserVO baseUserVO){
        return new ResponseEntity<>(departmentService.addDepartment(baseDepartmentVO, baseUserVO), HttpStatus.OK);
    }

    /**
     * 获取所有部门  树形结构
     * @return
     */
    @GetMapping
    public ResponseEntity<List<BaseDepartmentVO>> getDepartmentTree(){
        return new ResponseEntity<>(departmentService.getDepartmentTree(), HttpStatus.OK);
    }

    /**
     * 删除部门 todo 暂不处理order_num
     * todo 目前可以删除含有子节点的部门 这里是否要控制？
     * @param departmentId
     * @param baseUserVO
     */
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long departmentId, @LoginUser BaseUserVO baseUserVO){
        return new ResponseEntity<>(departmentService.deleteDepartment(departmentId, baseUserVO), HttpStatus.OK);
    }

    /**
     * 更新部门信息
     * @param baseDepartmentVO
     * @param baseUserVO
     * @return
     */
    @PutMapping
    public ResponseEntity<BaseDepartmentVO> updateDepartment(@RequestBody BaseDepartmentVO baseDepartmentVO, @LoginUser BaseUserVO baseUserVO){
        return new ResponseEntity<>(departmentService.updateDepartment(baseDepartmentVO, baseUserVO), HttpStatus.OK);
    }

}
