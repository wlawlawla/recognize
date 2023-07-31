package com.recognize.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.user.entity.BaseDepartmentEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BaseDepartmentMapper extends BaseMapper<BaseDepartmentEntity> {

    /**
     * 获取排序数字
     * @param parentId
     * @return
     */
    Integer getMaxOrderNumber(@Param("parentId") Long parentId);

    /**
     * 查询所有部门
     * @return
     */
    List<BaseDepartmentEntity> findAll();

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    BaseDepartmentEntity findById(@Param("id") Long id);
}
