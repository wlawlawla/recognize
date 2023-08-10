package com.recognize.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.task.entity.TaskRelationEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRelationMapper extends BaseMapper<TaskRelationEntity> {

    /**
     * 查询任务关联信息
     * @param taskId
     * @return
     */
    List<TaskRelationEntity> findByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务id删除关联信息
     * @param taskId
     */
    void deleteByTaskId(@Param("taskId") Long taskId);

}
