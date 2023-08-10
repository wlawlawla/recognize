package com.recognize.task.constant;

public enum RelationType {
    /**
     * 设备
     */
    DEVICE(1),

    /**
     * 负责人
     */
    DIRECTOR_USER(2),

    /**
     * 工作组成员
     */
    WORKER_USER(3)


    ;

    private Integer type;

    public Integer getType() {
        return type;
    }

    RelationType(Integer type){
        this.type = type;
    }
}
