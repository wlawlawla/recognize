package com.recognize.onnx.yolo;

import ai.onnxruntime.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.util.ImageUtil;
import lombok.Data;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class  Yolo {

    public static final int INPUT_SIZE = 640;
    public static final int NUM_INPUT_ELEMENTS = 3 * 640 * 640;
    public static final long[] INPUT_SHAPE = {1, 3, 640, 640};
    public float nmsThreshold;
    public OnnxJavaType inputType;
    protected OrtEnvironment env;
    protected OrtSession session;
    protected String inputName;
    public ArrayList<String> labelNames = new ArrayList<>();
    public Map<String, Integer> labelMap = new HashMap<>(16);

    OnnxTensor inputTensor;

    public Yolo(String modelPath, String labelPath, float nmsThreshold, int gpuDeviceId) throws OrtException, IOException {
        nu.pattern.OpenCV.loadLocally();

        this.env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
        sessionOptions.addCPU(false);
        sessionOptions.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);

        if (gpuDeviceId >= 0) sessionOptions.addCUDA(gpuDeviceId);
        this.session = this.env.createSession(modelPath, sessionOptions);

        Map<String, NodeInfo> inputMetaMap = this.session.getInputInfo();
        this.inputName = this.session.getInputNames().iterator().next();
        NodeInfo inputMeta = inputMetaMap.get(this.inputName);
        this.inputType = ((TensorInfo) inputMeta.getInfo()).type;

        this.nmsThreshold = nmsThreshold;

        BufferedReader br = new BufferedReader(new FileReader(labelPath));
        String line;
        while ((line = br.readLine()) != null) {
            this.labelNames.add(line);
        }
        if (CollectionUtils.isNotEmpty(this.labelNames)){
            this.labelMap.putAll(this.labelNames.stream().collect(Collectors.toMap(name -> name, name -> this.labelNames.indexOf(name))));
        }

    }

    private float computeIOU(float[] box1, float[] box2) {

        float area1 = (box1[2] - box1[0]) * (box1[3] - box1[1]);
        float area2 = (box2[2] - box2[0]) * (box2[3] - box2[1]);

        float left = Math.max(box1[0], box2[0]);
        float top = Math.max(box1[1], box2[1]);
        float right = Math.min(box1[2], box2[2]);
        float bottom = Math.min(box1[3], box2[3]);

        float interArea = Math.max(right - left, 0) * Math.max(bottom - top, 0);
        float unionArea = area1 + area2 - interArea;
        return Math.max(interArea / unionArea, 1e-8f);

    }

    protected List<float[]> nonMaxSuppression(List<float[]> bboxes, float iouThreshold) {

        // output boxes
        List<float[]> bestBboxes = new ArrayList<>();

        // confidence 순 정렬
        bboxes.sort(Comparator.comparing(a -> a[4]));

        // standard nms
        while (!bboxes.isEmpty()) {
            float[] bestBbox = bboxes.remove(bboxes.size() - 1);  // 현재 가장 confidence 가 높은 박스 pop
            bestBboxes.add(bestBbox);
            bboxes = bboxes.stream().filter(a -> computeIOU(a, bestBbox) < iouThreshold).collect(Collectors.toList());
        }

        return bestBboxes;
    }

    protected void xywh2xyxy(float[] bbox) {
        float x = bbox[0];
        float y = bbox[1];
        float w = bbox[2];
        float h = bbox[3];

        bbox[0] = x - w * 0.5f;
        bbox[1] = y - h * 0.5f;
        bbox[2] = x + w * 0.5f;
        bbox[3] = y + h * 0.5f;
    }

    protected void scaleCoords(float[] bbox, float orgW, float orgH, float padW, float padH, float gain) {
        // xmin, ymin, xmax, ymax -> (xmin_org, ymin_org, xmax_org, ymax_org)
        bbox[0] = Math.max(0, Math.min(orgW - 1, (bbox[0] - padW) / gain));
        bbox[1] = Math.max(0, Math.min(orgH - 1, (bbox[1] - padH) / gain));
        bbox[2] = Math.max(0, Math.min(orgW - 1, (bbox[2] - padW) / gain));
        bbox[3] = Math.max(0, Math.min(orgH - 1, (bbox[3] - padH) / gain));
    }

    static int argmax(float[] a) {
        float re = -Float.MAX_VALUE;
        int arg = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] >= re) {
                re = a[i];
                arg = i;
            }
        }
        return arg;
    }

    public List<Detection> run(Mat img, float conf) throws OrtException {

        float orgW = (float) img.size().width;
        float orgH = (float) img.size().height;

        float gain = Math.min((float) INPUT_SIZE / orgW, (float) INPUT_SIZE / orgH);
        float padW = (INPUT_SIZE - orgW * gain) * 0.5f;
        float padH = (INPUT_SIZE - orgH * gain) * 0.5f;

        // preprocessing
        Map<String, OnnxTensor> inputContainer = this.preprocess(img);

        // Run inference
        float[][] predictions;

        OrtSession.Result results = this.session.run(inputContainer);
        predictions = ((float[][][]) results.get(0).getValue())[0];

        // postprocessing
        return postprocess(predictions, orgW, orgH, padW, padH, gain, conf);
    }


    public Map<String, OnnxTensor> preprocess(Mat img) throws OrtException {

        // Resizing
        Mat resizedImg = new Mat();
        ImageUtil.resizeWithPadding(img, resizedImg, INPUT_SIZE, INPUT_SIZE);

        // BGR -> RGB
        Imgproc.cvtColor(resizedImg, resizedImg, Imgproc.COLOR_BGR2RGB);

        Map<String, OnnxTensor> container = new HashMap<>();

        // if model is quantized
        if (this.inputType.equals(OnnxJavaType.UINT8)) {
            byte[] whc = new byte[NUM_INPUT_ELEMENTS];
            resizedImg.get(0, 0, whc);
            byte[] chw = ImageUtil.whc2cwh(whc);
            ByteBuffer inputBuffer = ByteBuffer.wrap(chw);
            inputTensor = OnnxTensor.createTensor(this.env, inputBuffer, INPUT_SHAPE, this.inputType);
        } else {
            // Normalization
            resizedImg.convertTo(resizedImg, CvType.CV_32FC1, 1. / 255);
            float[] whc = new float[NUM_INPUT_ELEMENTS];
            resizedImg.get(0, 0, whc);
            float[] chw = ImageUtil.whc2cwh(whc);
            FloatBuffer inputBuffer = FloatBuffer.wrap(chw);
            inputTensor = OnnxTensor.createTensor(this.env, inputBuffer, INPUT_SHAPE);
        }

        // To OnnxTensor
        container.put(this.inputName, inputTensor);

        return container;
    }

    public List<Detection> postprocess(float[][] outputs, float orgW, float orgH, float padW, float padH, float gain, float confThreshold) {

        // predictions
        outputs = transposeMatrix(outputs);
        Map<Integer, List<float[]>> class2Bbox = new HashMap<>();

        for (float[] bbox : outputs) {


            float[] conditionalProbabilities = Arrays.copyOfRange(bbox, 4, 84);
            int label = Yolo.argmax(conditionalProbabilities);
            float conf = conditionalProbabilities[label];
            if (conf < confThreshold) continue;

            bbox[4] = conf;

            // xywh to (x1, y1, x2, y2)
            xywh2xyxy(bbox);

            // skip invalid predictions
            if (bbox[0] >= bbox[2] || bbox[1] >= bbox[3]) continue;

            // xmin, ymin, xmax, ymax -> (xmin_org, ymin_org, xmax_org, ymax_org)
            scaleCoords(bbox, orgW, orgH, padW, padH, gain);
            class2Bbox.putIfAbsent(label, new ArrayList<>());
            class2Bbox.get(label).add(bbox);
        }

        // Apply Non-max suppression for each class
        List<Detection> detections = new ArrayList<>();
        for (Map.Entry<Integer, List<float[]>> entry : class2Bbox.entrySet()) {
            int label = entry.getKey();
            List<float[]> bboxes = entry.getValue();
            bboxes = nonMaxSuppression(bboxes, this.nmsThreshold);
            for (float[] bbox : bboxes) {
                String labelString = this.labelNames.get(label);
                detections.add(new Detection(labelString, Arrays.copyOfRange(bbox, 0, 4), bbox[4]));
            }
        }

        return detections;
    }

    public static float[][] transposeMatrix(float [][] m){
        float[][] temp = new float[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }

    public Yolo(float confThreshold, float nmsThreshold, int gpuDeviceId) throws OrtException, IOException {

    }
}
