package com.recognize.common.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttachmentVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 外键
     */
    private Long foreignKeySid;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件类型
     */
    private Integer attachmentType;

    /**
     * 附件大小
     */
    private Double sizeInMb;
}
