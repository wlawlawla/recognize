package com.recognize.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "base_attachment")
public class BaseAttachmentEntity implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "fk_sid")
    private Long foreignKeySid;

    @TableField(value = "attachment_name")
    private String attachmentName;

    @TableField(value = "attachment_type")
    private Integer attachmentType;

    @TableField(value = "content")
    private byte[] content;

    @TableField(value = "sizeIn_mb")
    private Double sizeInMb;

}
