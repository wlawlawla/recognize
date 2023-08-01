package com.recognize.onnx.yolo;

import ai.onnxruntime.OrtException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class YoloModelSoft extends Yolo{

    public YoloModelSoft(String modelPath, float nmsThreshold, int gpuDeviceId, File modelFile, List<String> label) throws OrtException, IOException {
        super(modelPath, nmsThreshold, gpuDeviceId, modelFile, label);
    }

}
