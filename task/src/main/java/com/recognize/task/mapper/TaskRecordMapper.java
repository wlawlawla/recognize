package com.recognize.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.task.entity.TaskRecordEntity;
import com.recognize.task.entity.TaskRelationEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRecordMapper extends BaseMapper<TaskRecordEntity> {

    /**
     * 查询任务记录信息
     * @param taskId
     * @return
     */
    List<TaskRecordEntity> findByTaskId(@Param("taskId") Long taskId);
}
