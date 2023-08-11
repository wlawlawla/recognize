package com.recognize.task.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.common.common.AttachmentType;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.constant.BaseConstants;
import com.recognize.common.service.IAttachmentService;
import com.recognize.common.service.IConstantService;
import com.recognize.common.util.DateUtils;
import com.recognize.common.util.VOUtil;
import com.recognize.common.vo.AttachmentVO;
import com.recognize.common.vo.ConstantVO;
import com.recognize.device.service.IStrapService;
import com.recognize.device.vo.StrapDetailVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.onnx.service.IDetectionService;
import com.recognize.onnx.vo.DetectionSortVO;
import com.recognize.onnx.yolo.Detection;
import com.recognize.task.entity.TaskRecognizeEntity;
import com.recognize.task.entity.TaskRecordEntity;
import com.recognize.task.mapper.TaskRecognizeMapper;
import com.recognize.task.service.ITaskRecognizeService;
import com.recognize.task.vo.TaskRecognizeVO;
import com.recognize.user.vo.BaseUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@TranslationalService
@Slf4j
public class TaskRecognizeServiceImpl implements ITaskRecognizeService {

    @Autowired
    private TaskRecognizeMapper taskRecognizeMapper;

    @Autowired
    private IDetectionService detectionService;

    @Autowired
    private IStrapService strapService;

    @Autowired
    private IAttachmentService attachmentService;

    @Autowired
    private IConstantService constantService;

    private Map<String, String> strapValueMap = new HashMap<>();

    @PostConstruct
    private void init(){
        List<ConstantVO> strapStatusList = constantService.getConstantByType(BaseConstants.HARD_STRAP_STATUS);
        strapValueMap.putAll(strapStatusList.stream().collect(Collectors.toMap(ConstantVO::getCode, ConstantVO::getValue)));
    }

    @Override
    public TaskRecognizeVO analysisScreenImage(Long taskId, Long screenId, MultipartFile file, BaseUserVO currentUser) throws IOException {
        if (taskId == null || screenId == null){
            log.error("taskId or screenId is null");
            return null;
        }

        StrapScreenVO strapScreenVO = strapService.getStrapScreenInfo(screenId);
        if (strapScreenVO == null){
            log.error("can not find screen by id");
            return null;
        }

        Integer size = strapScreenVO.getStrapNumber();
        if (CollectionUtils.isNotEmpty(strapScreenVO.getStrapDetailVOList())){
            //压板数量以有定值设定的数量为准
            size = strapScreenVO.getStrapDetailVOList().size();
        }

        List<StrapDetailVO> strapDetailVOList = strapScreenVO.getStrapDetailVOList();
        strapDetailVOList.sort(Comparator.comparing(StrapDetailVO::getStrapPosition));

        byte[] bytes = file.getBytes();
        Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        List<DetectionSortVO> result = detectionService.recognize(img, size, strapScreenVO.getScreenType());
        List<Detection> drawList = result.stream().flatMap(detectionSortVO -> detectionSortVO.getDetectionList().stream()).collect(Collectors.toList());
        List<Detection> successDetectionList = new ArrayList<>();
        //todo 目前认为所有压板的行列数都不超过9，即压板位置是两位数字,第一位是行，第二位是列
        strapDetailVOList.forEach(strapDetailVO -> {
            Integer strapPosition = strapDetailVO.getStrapPosition();
            try {
                int rowNumber = strapPosition / 10 - 1;
                int colNumber = strapPosition % 10 - 1;
                Detection detection = result.get(rowNumber).getDetectionList().get(colNumber);
                if (strapValueMap.get(detection.getLabel()
                        .replace("hard_", "")
                        .replace("soft_", "")
                        .replace("_v2", "")
                        .replace("_v3", "")
                        .replace("on", "open")
                        .replace("off", "close"))
                        .equals(strapDetailVO.getStrapValue())) {
                    successDetectionList.add(detection);
                }
            } catch (Exception e) {
                log.error("识别校对异常");
            }
        });


        drawList.removeAll(successDetectionList);

        String name = DateUtils.NUMBER_DATE_TIME_FORMAT.format(LocalDateTime.now()) + ".jpg";
        detectionService.drawAndWriteImage(img, name, strapScreenVO.getScreenType(), drawList);

        TaskRecognizeEntity taskRecognizeEntity = new TaskRecognizeEntity();
        taskRecognizeEntity.setCreateBy(currentUser.getUserId());
        taskRecognizeEntity.setUpdateBy(currentUser.getUserId());
        taskRecognizeEntity.setTaskId(taskId);
        taskRecognizeEntity.setRecognizeNumber(drawList.size() + successDetectionList.size());
        taskRecognizeEntity.setErrorNumber(drawList.size());
        taskRecognizeEntity.setScreenId(screenId);
        taskRecognizeEntity.setDeviceId(strapScreenVO.getDeviceId());
        taskRecognizeEntity.setStrapNumber(size);

        taskRecognizeMapper.insert(taskRecognizeEntity);

        File image = FileUtils.getFile(name);
        AttachmentVO attachmentVO = attachmentService.uploadAttachment(image, AttachmentType.CHECK_TASK_IMAGE.getType(), taskRecognizeEntity.getId());

        taskRecognizeEntity.setAttachmentId(attachmentVO.getId());
        taskRecognizeMapper.updateById(taskRecognizeEntity);

        return getTaskRecognizeVOList(Arrays.asList(taskRecognizeEntity)).get(0);
    }

    /**
     * 转换成vo对象
     * @param taskRecognizeEntityList
     * @return
     */
    private List<TaskRecognizeVO> getTaskRecognizeVOList(List<TaskRecognizeEntity> taskRecognizeEntityList){
        if (CollectionUtils.isEmpty(taskRecognizeEntityList)){
            return Collections.emptyList();
        }

        List<TaskRecognizeVO> taskRecognizeVOList = new ArrayList<>();
        taskRecognizeEntityList.forEach(taskRecognizeEntity -> {
            TaskRecognizeVO taskRecognizeVO = VOUtil.getVO(TaskRecognizeVO.class, taskRecognizeEntity);
            taskRecognizeVO.setImageUrl(taskRecognizeEntity.getAttachmentId());
            taskRecognizeVOList.add(taskRecognizeVO);
        });
        return taskRecognizeVOList;
    }


}
