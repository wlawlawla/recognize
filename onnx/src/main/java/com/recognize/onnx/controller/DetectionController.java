package com.recognize.onnx.controller;

import ai.onnxruntime.OrtException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.onnx.service.IDetectionService;
import com.recognize.onnx.yolo.Detection;
import com.recognize.util.ImageUtil;
import com.recognize.onnx.yolo.YoloModelHard;
import com.recognize.onnx.yolo.YoloModelSoft;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class DetectionController {
    static private final List<String> mimeTypes = Arrays.asList("image/png", "image/jpeg");
    @Autowired
    private YoloModelSoft yoloModelSoft;
    @Autowired
    private YoloModelHard yoloModelHard;
    @Autowired
    private IDetectionService detectionService;

    private Float PARAM_CONF = 0.47f;

    @PostMapping(value = "/detection/hard")
    public List<Detection> detectionHard(@RequestParam(name = "uploadFile") MultipartFile uploadFile,
                                         @RequestParam(name = "size") Integer size) throws OrtException, IOException {
        if (!mimeTypes.contains(uploadFile.getContentType())) {
            return null;
        }
        List<Detection> result = detectionService.recognize(yoloModelHard, uploadFile, size);
        return result;
    }

/*    @PostMapping(value = "/detection/hard")
    public List<Detection> detectionHard(@RequestParam(name = "uploadFile") MultipartFile uploadFile,
                                         @RequestParam(name = "c", defaultValue = "0.47f") Float c,
                                         @RequestParam(name = "n", defaultValue = "0.5f") Float n) throws OrtException, IOException {
        if (!mimeTypes.contains(uploadFile.getContentType())) {
            return null;
        }
        yoloModelHard.setNmsThreshold(n);
        byte[] bytes = uploadFile.getBytes();
        Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        List<Detection> result = yoloModelHard.run(img, PARAM_CONF);
        if (CollectionUtils.isNotEmpty(result)){
            result.sort(Comparator.comparing(Detection::getConfidence));
            Collections.reverse(result);
            for (int i = 0; i < result.size(); i++){
                result.get(i).setLabel(result.get(i).getLabel() + i);
            }
        }
        Map<String, Integer> labelMap = yoloModelHard.getLabelNames().stream().collect(Collectors.toMap(name -> name, name -> yoloModelHard.getLabelNames().indexOf(name)));

        ImageUtil.drawPredictions(img, result, labelMap);

        Imgcodecs.imwrite("predictions.jpg", img);
        return result;
    }*/

    @PostMapping(value = "/detection/soft")
    public List<Detection> detectionSoft(@RequestParam(name = "uploadFile") MultipartFile uploadFile) throws OrtException, IOException {
        if (!mimeTypes.contains(uploadFile.getContentType())) {
            return null;
        }
        byte[] bytes = uploadFile.getBytes();
        Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        List<Detection> result = yoloModelSoft.run(img, PARAM_CONF);
        Map<String, Integer> labelMap = yoloModelSoft.getLabelNames().stream().collect(Collectors.toMap(name -> name, name -> yoloModelSoft.getLabelNames().indexOf(name)));

        ImageUtil.drawPredictions(img, result, labelMap);

        Imgcodecs.imwrite("predictions.jpg", img);
        return result;
    }
}
