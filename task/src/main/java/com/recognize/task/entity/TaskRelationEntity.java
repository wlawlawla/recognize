package com.recognize.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "task_relation")
public class TaskRelationEntity implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "relation_type")
    private Integer relationType;

    @TableField(value = "task_id")
    private Long taskId;

    @TableField(value = "relation_id")
    private Long relationId;

    public TaskRelationEntity(Integer relationType, Long taskId, Long relationId){
        this.relationType = relationType;
        this.taskId = taskId;
        this.relationId = relationId;
    }

}