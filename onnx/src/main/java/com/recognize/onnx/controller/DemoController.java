package com.recognize.onnx.controller;

import ai.onnxruntime.OrtException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.onnx.yolo.*;
import com.recognize.onnx.util.ImageUtil;
import com.recognize.onnx.util.LabelImgUtil;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test")
public class DemoController {
    @Autowired
    private YoloModelSoft yoloModelSoft;

    @Autowired
    private YoloModelHard yoloModelHard;

    private float PARAM_CONF = 0.47f;

    @GetMapping("/g")
    public void test7(){
        ArrayList<String> labelNames = yoloModelSoft.getLabelNames();
        Map<String, Integer> labelMap = labelNames.stream().collect(Collectors.toMap(name -> name, name -> labelNames.indexOf(name)));

        File file = new File("C:\\Users\\wangjd\\Desktop\\soft\\images\\test");
        String targePathStr = "C:\\Users\\wangjd\\Desktop\\soft\\images\\recog" + "\\";
        int i = 0;
        for (File child : file.listFiles()){
            try {
                FileInputStream fis;
                byte[] buffer = null;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                fis = new FileInputStream(child);
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                fis.close();
                bos.close();
                buffer = bos.toByteArray();

                String name = child.getName();

                /*File dir =  new File(targePathStr + i);
                dir.mkdir();
                Files.copy(child.toPath(), new File(targePathStr + i + "\\" + name).toPath());*/
                Mat img = Imgcodecs.imdecode(new MatOfByte(buffer), Imgcodecs.IMREAD_COLOR);

                List<Detection> result = yoloModelSoft.run(img, PARAM_CONF);
                ImageUtil.drawPredictions(img, result, labelMap);
                Imgcodecs.imwrite(targePathStr + name /*+ i + ".jpg"*/, img);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @PostMapping("/1w")
    public void w(@RequestParam(name = "uploadFile") MultipartFile uploadFile) throws OrtException, IOException {
        String targePathStr = "D:\\img" + "\\";
        byte[] bytes = uploadFile.getBytes();
        Map<String, Integer> labelMap = yoloModelHard.getLabelNames().stream().collect(Collectors.toMap(name -> name, name -> yoloModelHard.getLabelNames().indexOf(name)));
        System.out.println(LocalDateTime.now() + "--------start");
        for (float conf = 0.00f; conf < 1.00f; conf = conf + 0.01f){
            for (float nms = 0.00f; nms < 1.00f; nms = nms + 0.01f){
                Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
                yoloModelHard.setNmsThreshold(nms);
                List<Detection> result = yoloModelHard.run(img, PARAM_CONF);
                if (CollectionUtils.isNotEmpty(result) && result.size() == 9){
                    result.sort(Comparator.comparing(Detection::getConfidence));
                    Collections.reverse(result);
                    for (int i = 0; i < result.size(); i++){
                        result.get(i).setLabel(result.get(i).getLabel() + i);
                    }
                    ImageUtil.drawPredictions(img, result, labelMap);
                    Imgcodecs.imwrite(targePathStr + "c" + conf + "n"+ nms + ".jpg", img);
                }
            }
        }
        System.out.println(LocalDateTime.now() + "--------end");

    }

    @GetMapping("/label")
    public void test1(){
        ArrayList<String> labelNames = yoloModelHard.getLabelNames();
        Map<String, Integer> labelMap = labelNames.stream().collect(Collectors.toMap(name -> name, name -> labelNames.indexOf(name)));

        File file = new File("C:\\Users\\wangjd\\Desktop\\images\\test");
        for (File child : file.listFiles()){
            try {
                FileInputStream fis;
                byte[] buffer = null;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                fis = new FileInputStream(child);
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                fis.close();
                bos.close();
                buffer = bos.toByteArray();

                String name = child.getName();

                Mat img = Imgcodecs.imdecode(new MatOfByte(buffer), Imgcodecs.IMREAD_COLOR);

                List<Detection> result = yoloModelHard.run(img, PARAM_CONF);
                String labelInfo = LabelImgUtil.setLabelInfo(result, file);

                String labelFileName = "C:\\Users\\wangjd\\Desktop\\images\\ts\\" + name.substring(0, name.indexOf("."))+ ".xml";
                try (FileWriter fw = new FileWriter(labelFileName)) {
                    fw.write(labelInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
