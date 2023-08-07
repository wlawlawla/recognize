package com.recognize.task.service;

import com.recognize.task.dto.TaskInfoDto;
import com.recognize.task.vo.TaskInfoVO;
import com.recognize.user.vo.BaseUserVO;

public interface ITaskService {

    /**
     * 新增任务
     * @param taskInfoDto
     * @param currentUser
     * @return
     */
    TaskInfoVO addTask(TaskInfoDto taskInfoDto, BaseUserVO currentUser);
}
