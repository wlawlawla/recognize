package com.recognize.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.task.entity.TaskDetailEntity;
import com.recognize.task.entity.TaskRelationEntity;
import com.recognize.task.parameter.TaskSearchParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDetailMapper extends BaseMapper<TaskDetailEntity> {


    /**
     * 查询任务
     * @param taskId
     * @return
     */
    TaskDetailEntity findByTaskId(@Param("taskId") Long taskId);

    /**
     * 任务查询
     * @param page
     * @param searchParameter
     * @return
     */
    Page<TaskDetailEntity> searchTask(Page page, @Param("param")TaskSearchParameter searchParameter);

    /**
     * 查询当前任务列表
     * @param userId
     * @param relationType
     * @return
     */
    List<TaskDetailEntity> getCurrentTask(@Param("userId") Long userId, @Param("relationType") Integer relationType);
}
