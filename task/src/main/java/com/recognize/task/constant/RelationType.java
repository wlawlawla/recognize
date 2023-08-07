package com.recognize.task.constant;

public enum RelationType {
    /**
     * 设备
     */
    DEVICE(1),

    /**
     *
     */
    DIRECTOR_USER(2),

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
