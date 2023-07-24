package com.recognize.onnx.yolo;

import ai.onnxruntime.OrtException;

import java.io.IOException;

public class YoloModelHard extends Yolo{

    public YoloModelHard(String modelPath, String labelPath, float nmsThreshold, int gpuDeviceId) throws OrtException, IOException {
        super(modelPath, labelPath, nmsThreshold, gpuDeviceId);
    }
}
