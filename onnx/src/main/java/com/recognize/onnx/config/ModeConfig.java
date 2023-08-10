package com.recognize.onnx.config;

import ai.onnxruntime.OrtException;
import com.recognize.common.util.FileUtil;
import com.recognize.onnx.yolo.YoloModelHard;
import com.recognize.onnx.yolo.YoloModelSoft;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ModeConfig {

    @Value("${model.config.soft.modelPath}")
    private String softModelPath;

    @Value("${model.config.soft.label}")
    private String softLabel;

    @Value("${model.config.hard.modelPath}")
    private String HardModelPath;

    @Value("${model.config.hard.label}")
    private String hardLabel;

    @Value("${model.config.nmsThreshold}")
    private String nmsThreshold;

    @Value("${model.config.gpuDeviceId}")
    private String gpuDeviceId;

    /**
     * 软压板模型识别
     * @return
     * @throws IOException
     * @throws OrtException
     */
    @Bean(name = "ModelSoft")
    public YoloModelSoft getSoft() throws IOException, OrtException{
        File modelFile = FileUtil.getFile(this, softModelPath, softModelPath);
        String modelPathReal = modelFile.getCanonicalPath();
        return new YoloModelSoft(modelPathReal, Float.parseFloat(nmsThreshold), Integer.parseInt(gpuDeviceId), modelFile, Arrays.asList(softLabel.replace(" ", "").split(",")));
    }

    /**
     * 硬压板模型识别
     * @return
     * @throws IOException
     * @throws OrtException
     */
    @Bean(name = "ModelHard")
    public YoloModelHard getHard() throws IOException, OrtException{
        File modelFile = FileUtil.getFile(this, HardModelPath, HardModelPath);
        String modelPathReal = modelFile.getCanonicalPath();
        return new YoloModelHard(modelPathReal, Float.parseFloat(nmsThreshold), Integer.parseInt(gpuDeviceId), modelFile, Arrays.asList(hardLabel.replace(" ", "").split(",")));
    }


}
