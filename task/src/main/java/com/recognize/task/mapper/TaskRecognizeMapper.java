package com.recognize.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.task.entity.TaskRecognizeEntity;
import com.recognize.task.parameter.TaskSearchParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRecognizeMapper extends BaseMapper<TaskRecognizeEntity> {


}
