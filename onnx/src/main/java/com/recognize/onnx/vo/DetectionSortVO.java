package com.recognize.onnx.vo;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.onnx.yolo.Detection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DetectionSortVO {

    Float maxY;

    Float minY;

    List<Detection> detectionList;

    public DetectionSortVO(Detection detection){
        this.maxY = detection.getBbox()[3];
        this.minY = detection.getBbox()[1];
        detectionList = new ArrayList<>();
        detectionList.add(detection);
    }

    public void insertAndUpdateMaxYAndMinY(Detection detection){
        this.detectionList.add(detection);
        this.minY = Math.min(detection.getBbox()[1], this.minY);
        this.maxY = Math.max(detection.getBbox()[3], this.maxY);
    }
}
