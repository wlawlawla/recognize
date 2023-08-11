package com.recognize.task.vo;

import com.recognize.common.constant.BaseConstants;
import lombok.Data;

@Data
public class TaskRecognizeVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 屏幕id
     */
    private Long screenId;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 压板数量
     */
    private Integer strapNumber;

    /**
     * 异常数量
     */
    private Integer errorNumber;

    /**
     * 屏幕id
     */
    private String imageUrl;

    public void setImageUrl(Long id){
        if (id != null){
            this.imageUrl = BaseConstants.IMAGE_URL_PREFIX + id;
        }
    }

}
