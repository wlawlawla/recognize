package com.recognize.onnx.service.impl;

import ai.onnxruntime.OrtException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.constant.BaseConstants;
import com.recognize.common.service.IConstantService;
import com.recognize.common.vo.ConstantVO;
import com.recognize.onnx.service.IDetectionService;
import com.recognize.onnx.yolo.Detection;
import com.recognize.onnx.yolo.Yolo;
import com.recognize.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@TranslationalService
public class DetectionServiceImpl implements IDetectionService {

    @Autowired
    private IConstantService constantService;

    //标记优先级
    private Map<String, Integer> levelMap = new HashMap<>();

    //识别参数 默认设置，可以通过修改数据库表base_constant where type = yolo_param 调整
    private Float PARAM_CONF = 0.47f;

    @PostConstruct
    private void init() {
        //初始化标记优先级
        List<ConstantVO> hardLevelList = constantService.getConstantByType(BaseConstants.CONSTANT_TYPE_LEVEL);
        if (CollectionUtils.isNotEmpty(hardLevelList)){
            levelMap.putAll(hardLevelList.stream().collect(Collectors.toMap(ConstantVO::getCode, constant -> Integer.valueOf(constant.getValue()))));
        }

        //初始化识别参数
        List<ConstantVO> yoloParam = constantService.getConstantByType(BaseConstants.CONSTANT_TYPE_YOLO);
        if (CollectionUtils.isNotEmpty(yoloParam)){
            yoloParam.forEach(param -> {
                if (BaseConstants.CODE_YOLO_CONF.equals(param.getCode())){
                    PARAM_CONF = Float.valueOf(param.getValue());
                }
            });
        }
    }


    @Override
    public List<Detection> recognize(Yolo yoloModel, MultipartFile uploadFile, int size) throws OrtException, IOException{
        byte[] bytes = uploadFile.getBytes();

        Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);

        List<Detection> result = recognize(yoloModel, img, size, PARAM_CONF);

        if (CollectionUtils.isNotEmpty(result)){
            result.sort(Comparator.comparing(Detection::getConfidence));
            Collections.reverse(result);
/*            for (int i = 0; i < result.size(); i++){
                result.get(i).setLabel(result.get(i).getLabel() + i);
            }*/
        }

        ImageUtil.drawPredictions(img, result, yoloModel.getLabelMap());

        Imgcodecs.imwrite("predictions.jpg", img);
        return result;
    }

    /**
     * 循环(识别-去重-调整参数)-返回结果
     * @param yoloModel
     * @param img
     * @return
     * @throws OrtException
     */
    private List<Detection> recognize(Yolo yoloModel, Mat img, int size, float conf) throws OrtException{
        List<Detection> result = filterDetections(yoloModel.run(img, conf));
        if (size == result.size() || conf < 0.01f){
            return result;
        }

        if (size < result.size()){
            result.sort(Comparator.comparing(Detection::getConfidence));
            for (int i = result.size() - size; i > 0 ; i--){
                result.remove(0);
            }
            return result;
        }

        //识别数量少于预定数量，降低置信度重新识别
        conf = conf / 2f;
        return recognize(yoloModel, img, size, conf);
    }


    /**
     * @param detectionList
     * 过滤识别框
     * @return
     */
    public List<Detection> filterDetections(List<Detection> detectionList){
        if (CollectionUtils.isNotEmpty(detectionList)){
            for (Detection a : detectionList){
                for (Detection b : detectionList){
                    if (!a.equals(b)){
                        deleteDuplicated(a, b);
                    }
                }
            }
        }
        return detectionList.stream().filter(detection -> !detection.isDel()).collect(Collectors.toList());
    }

    /**
     * 去除重复框
     *
     * @param a
     * @param b
     */
    private void deleteDuplicated(Detection a, Detection b) {
        if (a == null || b == null || a.isDel() || b.isDel()) {
            return;
        }

        List<Float> axList = Stream.of(a.getBbox()[0], a.getBbox()[2]).collect(Collectors.toList());
        List<Float> ayList = Stream.of(a.getBbox()[1], a.getBbox()[3]).collect(Collectors.toList());
        List<Float> bxList = Stream.of(b.getBbox()[0], b.getBbox()[2]).collect(Collectors.toList());
        List<Float> byList = Stream.of(b.getBbox()[1], b.getBbox()[3]).collect(Collectors.toList());
        Collections.sort(axList);
        Collections.sort(ayList);
        Collections.sort(bxList);
        Collections.sort(byList);

        List<Float> innerPointAX = new ArrayList<>();
        List<Float> innerPointAY = new ArrayList<>();
        setInnerPoint(axList, ayList, bxList, byList, innerPointAX, innerPointAY);

        if (innerPointAX.size() > 0) {
            if (innerPointAX.size() == 4){
                //a在b内 去重
                updateDel(a, b);
                return;
            }

            a.setArea();
            b.setArea();
            Float area = null;
            if (innerPointAX.size() == 1) {
                //a有1个点在b内 则必然b有一个点在a内，这两点构成的区域就是重合区域，
                List<Float> innerPointBX = new ArrayList<>();
                List<Float> innerPointBY = new ArrayList<>();

                setInnerPoint(bxList, byList, axList, ayList, innerPointBX, innerPointBY);
                if (innerPointBX.size() != 1){
                    log.error("查找顶点异常");
                }
                //计算重合面积
                area = Math.abs(innerPointBX.get(0) - innerPointAX.get(0)) * Math.abs(innerPointBY.get(0) - innerPointAY.get(0));

            } else if (innerPointAX.size() == 2) {
                Float x1, y1, x2 = null, y2 = null;

                //a有2个点在b内
                if (innerPointAX.get(0).equals(innerPointAX.get(1))){
                    x1 = innerPointAX.get(0);
                    if (innerPointAY.get(0) > innerPointAY.get(1)){
                        y1 = innerPointAY.get(1);
                        y2 = innerPointAY.get(0);
                    } else {
                        y1 = innerPointAY.get(0);
                        y2 = innerPointAY.get(1);
                    }

                    for (Float bx : bxList){
                        if (axList.get(0) <= bx && axList.get(1) >= bx){
                            x2 = bx;
                            break;
                        }
                    }
                    if (x2 == null){
                        log.error("去重失败：没有找到第二个x坐标");
                        return;
                    }

                }else {
                    y1 = innerPointAY.get(0);
                    if (innerPointAX.get(0) > innerPointAX.get(1)){
                        x1 = innerPointAX.get(1);
                        x2 = innerPointAX.get(0);
                    } else {
                        x1 = innerPointAX.get(0);
                        x2 = innerPointAX.get(1);
                    }
                    for (Float by : byList){
                        if (ayList.get(0) <= by && ayList.get(1) >= by){
                            y2 = by;
                            break;
                        }
                    }
                    if (y2 == null){
                        log.error("去重失败：没有找到第二个y坐标");
                        return;
                    }
                }
                area = Math.abs(x1 - x2) * Math.abs(y1 - y2);
            }

            //判断重合面积是否达到去重要求
            if (checkUpdateDel(area, a.getArea(), b.getArea())){
                updateDel(a, b);
            }
        }
    }

    /**
     * 判断 (ax,ay) 是否在b的框内
     *
     * @param ax
     * @param ay
     * @param bxList
     * @param byList
     * @return
     */
    private boolean checkPointInner(Float ax, Float ay, List<Float> bxList, List<Float> byList) {
        if ((ax >= bxList.get(0) && ax <= bxList.get(1)) && (ay >= byList.get(0) && ay <= byList.get(1))) {
            return true;
        }
        return false;
    }

    /**
     * 删除重合框
     * @param a
     * @param b
     */
    private void updateDel(Detection a, Detection b) {
        if (a == null || b == null || a.isDel() || b.isDel()){
            return;
        }

        //识别结果相同，比较置信度，保留置信度较高的
        if (a.getLabel().equals(b.getLabel())){
            if (a.getConfidence() > b.getConfidence()){
                b.setDel(true);
            }else {
                a.setDel(true);
            }
            return;
        }

        //识别结果不同，按照标记优先级保留优先级高的
        Integer levelA = levelMap.get(a.getLabel());
        Integer levelB = levelMap.get(b.getLabel());

        if (levelA != null && levelB != null){
            if (levelA > levelB){
                b.setDel(true);
            }else {
                a.setDel(true);
            }
        }
        //如果没有查到标记的优先级，就删除
        if (levelA == null){
            log.warn("优先级缺失： label = " + a.getLabel());
            a.setDel(true);
        }
        if (levelB == null){
            log.warn("优先级缺失： label = " + b.getLabel());
            b.setDel(true);
        }

    }

    /**
     * 找出a的四个点中有哪些在b的区域范围之内
     * @param axList
     * @param ayList
     * @param bxList
     * @param byList
     * @param innerPointX
     * @param innerPointY
     */
    private void setInnerPoint(List<Float> axList, List<Float> ayList, List<Float> bxList, List<Float> byList,
                               List<Float> innerPointX, List<Float> innerPointY){
        for (Float ax : axList) {
            for (Float ay : ayList) {
                boolean inner = checkPointInner(ax, ay, bxList, byList);
                if (inner) {
                    innerPointX.add(ax);
                    innerPointY.add(ay);
                }
            }
        }
    }

    /**
     * 判断重合区域面积是否达到去重要求 : 重合50%
     * @param duplicatedArea
     * @param aArea
     * @param bArea
     * @return
     */
    private boolean checkUpdateDel(float duplicatedArea, float aArea, float bArea){
        if (duplicatedArea >= aArea / 2f || duplicatedArea >= bArea /2f){
            return true;
        }
        return false;
    }

}
