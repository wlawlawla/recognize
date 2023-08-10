package com.recognize.task.controller;

import com.recognize.common.exception.ResultErrorEnum;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.vo.PageVO;
import com.recognize.task.dto.TaskInfoDto;
import com.recognize.task.parameter.TaskSearchParameter;
import com.recognize.task.service.ITaskService;
import com.recognize.task.vo.TaskInfoVO;
import com.recognize.user.util.LoginUser;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    /**
     * 新建任务
     * @param taskInfoDto
     * @param currentUser
     * @return
     */
    @PostMapping
    public ResponseEntity<TaskInfoVO> addTask(@RequestBody TaskInfoDto taskInfoDto, @LoginUser BaseUserVO currentUser){
        return new ResponseEntity<>(taskService.saveTask(taskInfoDto, currentUser), HttpStatus.OK);
    }

    /**
     * 根据taskId获取任务信息
     * @param taskId
     * @return
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskInfoVO> getTaskInfo(@PathVariable Long taskId){
        return new ResponseEntity<>(taskService.getTaskInfo(taskId), HttpStatus.OK);
    }

    /**
     * 更新任务信息 todo 可更新的字段不明确
     * @param taskInfoDto
     * @param currentUser
     * @return
     */
    @PutMapping
    public ResponseEntity<TaskInfoVO> updateTask(@RequestBody TaskInfoDto taskInfoDto, @LoginUser BaseUserVO currentUser){
        if (taskInfoDto.getTaskId() == null){
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }
        return new ResponseEntity<>(taskService.saveTask(taskInfoDto, currentUser), HttpStatus.OK);
    }

    /**
     * 获取任务列表(分页)(搜索)
     * @param pageable
     * @param searchParameter
     * @return
     */
    @PostMapping("/search")
    public ResponseEntity<PageVO<TaskInfoVO>> searchTaskInfo(@PageableDefault(page = 1, size = 20) Pageable pageable, @RequestBody(required = false) TaskSearchParameter searchParameter){
        return new ResponseEntity<>(taskService.searchTask(pageable, searchParameter), HttpStatus.OK);
    }

    /**
     * 查询当前用户未完成的任务列表
     * @param currentUser
     * @param relationType 2负责人 3工作组成员  不传则默认查全部
     * @return
     */
    @GetMapping("/current")
    public ResponseEntity<List<TaskInfoVO>> getCurrentTask(@LoginUser BaseUserVO currentUser, @RequestParam(value = "relationType", required = false) Integer relationType){
        return new ResponseEntity<>(taskService.getCurrentTaskList(currentUser, relationType), HttpStatus.OK);
    }

}
