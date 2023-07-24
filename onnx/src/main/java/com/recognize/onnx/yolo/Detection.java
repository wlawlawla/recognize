package com.recognize.onnx.yolo;

import lombok.Data;

@Data
public class Detection {

    private String label;

    private float[] bbox;

    private float confidence;

    private boolean del;

    private Float area;

    public Detection(String label, float[] bbox, float confidence){
        this.label = label;
        this.bbox = bbox;
        this.confidence = confidence;
        del = false;
    }

    public void setArea() {
        if (this.area == null){
            this.area = Math.abs(this.bbox[2] - this.bbox[0])*Math.abs(this.bbox[3] - this.bbox[1]);
        }
    }
}
