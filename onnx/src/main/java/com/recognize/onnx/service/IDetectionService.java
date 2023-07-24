package com.recognize.onnx.service;

import ai.onnxruntime.OrtException;
import com.recognize.onnx.yolo.Detection;
import com.recognize.onnx.yolo.Yolo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IDetectionService {

    /**
     * 识别
     * @param yoloModel
     * @param uploadFile
     * @param size
     * @return
     * @throws OrtException
     * @throws IOException
     */
    List<Detection> recognize(Yolo yoloModel, MultipartFile uploadFile, int size) throws OrtException, IOException;
}
