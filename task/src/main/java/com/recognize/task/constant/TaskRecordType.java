package com.recognize.task.constant;

import com.recognize.task.vo.TaskInfoVO;

public enum TaskRecordType {
    /**
     * 任务信息
     */
    TASK_INFO("task_info", TaskInfoVO.class),

    /**
     * 屏幕照片id
     */
    SCREEN_IMAGE("screen_image_id", Long.class),
    ;

    /**
     * 数据类型名称
     */
    private String name;

    /**
     * 数据格式
     */
    private Object format;

    TaskRecordType(String name, Object o){
        this.name = name;
        this.format = o;
    }

    public Object getFormat(){
        return this.format;
    }

    public String getName(){
        return this.name;
    }
}
