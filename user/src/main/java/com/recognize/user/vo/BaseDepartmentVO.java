package com.recognize.user.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BaseDepartmentVO {

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 父级部门id
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank
    private String departmentName;

    /**
     * 排序
     */
    private Integer orderNumber;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子部门
     */
    private List<BaseDepartmentVO> children;
}
