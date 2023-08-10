package com.recognize.task.service;

import com.recognize.common.vo.PageVO;
import com.recognize.task.dto.TaskInfoDto;
import com.recognize.task.parameter.TaskSearchParameter;
import com.recognize.task.vo.TaskInfoVO;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITaskService {

    /**
     * 新增任务
     * @param taskInfoDto
     * @param currentUser
     * @return
     */
    TaskInfoVO saveTask(TaskInfoDto taskInfoDto, BaseUserVO currentUser);


    /**
     * 根据taskid获取任务信息
     * @param taskId
     * @return
     */
    TaskInfoVO getTaskInfo(Long taskId);

    /**
     * 搜索任务列表 分页
     * @param pageable
     * @param searchParameter
     * @return
     */
    PageVO<TaskInfoVO> searchTask(Pageable pageable, TaskSearchParameter searchParameter);

    /**
     * 获取当前用户的未完成任务
     * @param currentUser 当前用户
     * @param relationType 关联类型 ---》 RelationType  不传则查询全部关联类型
     * @return
     */
    List<TaskInfoVO> getCurrentTaskList(BaseUserVO currentUser, Integer relationType);
}
