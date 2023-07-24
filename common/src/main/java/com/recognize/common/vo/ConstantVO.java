package com.recognize.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class ConstantVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 类型
     */
    private String type;

    /**
     * 代码
     */
    private String code;

    /**
     * 值
     */
    private String value;

    /**
     * 父节点id
     */
    private String parentId;

    /**
     * 父节点code
     */
    private String parentCode;

    /**
     * 排序
     */
    private String orderNumber;

    /**
     * 子节点
     */
    private List<ConstantVO> children;
}
