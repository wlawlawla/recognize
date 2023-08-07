package com.recognize.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recognize.common.entity.BaseAttachmentEntity;
import com.recognize.common.entity.BaseConstantEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseAttachmentMapper extends BaseMapper<BaseAttachmentEntity> {

    /**
     * 查询附件
     * @param id
     * @return
     */
    BaseAttachmentEntity findById(@Param("id") Long id);

    /**
     * 查询附件
     * @param fkSid
     * @param type
     * @return
     */
    BaseAttachmentEntity findByFkSidAndType(@Param("fkSid") Long fkSid, @Param("type") Integer type);

    /**
     * 删除附件
     * @param id
     */
    void deleteByAttachmentId(@Param("id") Long id);

}
