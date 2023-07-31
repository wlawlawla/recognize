package com.recognize.onnx.config;

import ai.onnxruntime.OrtException;
import com.recognize.common.util.FileUtil;
import com.recognize.onnx.yolo.YoloModelHard;
import com.recognize.onnx.yolo.YoloModelSoft;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ModeConfig {

    @Value("${model.config.soft.modelPath}")
    private String softModelPath;

    @Value("${model.config.soft.labelPath}")
    private String softLabelPath;

    @Value("${model.config.hard.modelPath}")
    private String HardModelPath;

    @Value("${model.config.hard.labelPath}")
    private String HardLabelPath;

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
        String modelPathReal = FileUtil.getFile(this, softModelPath, softModelPath).getCanonicalPath();
        String labelPathReal = FileUtil.getFile(this, softLabelPath, softLabelPath).getCanonicalPath();
        return new YoloModelSoft(modelPathReal, labelPathReal, Float.parseFloat(nmsThreshold), Integer.parseInt(gpuDeviceId)) {
        };
    }

    /**
     * 硬压板模型识别
     * @return
     * @throws IOException
     * @throws OrtException
     */
    @Bean(name = "ModelHard")
    public YoloModelHard getHard() throws IOException, OrtException{
        String modelPathReal = FileUtil.getFile(this, HardModelPath, HardModelPath).getCanonicalPath();
        String labelPathReal = FileUtil.getFile(this, HardLabelPath, HardLabelPath).getCanonicalPath();
        return new YoloModelHard(modelPathReal, labelPathReal, Float.parseFloat(nmsThreshold), Integer.parseInt(gpuDeviceId));
    }


}
