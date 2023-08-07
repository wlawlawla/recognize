package com.recognize.task.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.util.VOUtil;
import com.recognize.device.service.IDeviceInfoService;
import com.recognize.device.vo.StationInfoVO;
import com.recognize.task.constant.RelationType;
import com.recognize.task.dto.TaskInfoDto;
import com.recognize.task.entity.TaskDetailEntity;
import com.recognize.task.entity.TaskRelationEntity;
import com.recognize.task.mapper.TaskDetailMapper;
import com.recognize.task.mapper.TaskRelationMapper;
import com.recognize.task.service.ITaskRelationService;
import com.recognize.task.service.ITaskService;
import com.recognize.task.vo.TaskInfoVO;
import com.recognize.user.service.IUserInfoService;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@TranslationalService
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private TaskDetailMapper taskDetailMapper;

    @Autowired
    private ITaskRelationService taskRelationService;

    @Autowired
    private TaskRelationMapper taskRelationMapper;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IDeviceInfoService deviceInfoService;

    @Override
    public TaskInfoVO addTask(TaskInfoDto taskInfoDto, BaseUserVO currentUser){
        if (taskInfoDto == null){
            return null;
        }

        TaskDetailEntity taskDetailEntity = VOUtil.getVO(TaskDetailEntity.class, taskInfoDto);
        taskDetailEntity.setCreateBy(currentUser.getUserId());
        taskDetailEntity.setUpdateBy(currentUser.getUserId());

        taskDetailMapper.insert(taskDetailEntity);

        List<TaskRelationEntity> taskRelationEntityList = new ArrayList<>();

        //分别保存设备、负责人、工作组成员关联信息
        if (CollectionUtils.isNotEmpty(taskInfoDto.getDeviceIds())){
            taskInfoDto.getDeviceIds().forEach(deviceId -> taskRelationEntityList.add(new TaskRelationEntity(RelationType.DEVICE.getType(), taskDetailEntity.getTaskId(), deviceId)));
        }

        if (CollectionUtils.isNotEmpty(taskInfoDto.getDirectorIds())){
            taskInfoDto.getDirectorIds().forEach(directorId -> taskRelationEntityList.add(new TaskRelationEntity(RelationType.DIRECTOR_USER.getType(), taskDetailEntity.getTaskId(), directorId)));
        }

        if (CollectionUtils.isNotEmpty(taskInfoDto.getWorkerIds())){
            taskInfoDto.getWorkerIds().forEach(workerId -> taskRelationEntityList.add(new TaskRelationEntity(RelationType.WORKER_USER.getType(), taskDetailEntity.getTaskId(), workerId)));
        }

        if (CollectionUtils.isNotEmpty(taskRelationEntityList)){
            taskRelationService.saveBatch(taskRelationEntityList);
        }

        return getTaskInfoVO(taskDetailEntity);
    }

    /**
     * 获取vo对象
     * @param taskDetailEntity
     * @return
     */
    private TaskInfoVO getTaskInfoVO(TaskDetailEntity taskDetailEntity){
        if (taskDetailEntity == null){
            return null;
        }

        TaskInfoVO taskInfoVO = VOUtil.getVO(TaskInfoVO.class, taskDetailEntity);

        List<TaskRelationEntity> taskRelationEntityList = taskRelationMapper.findByTaskId(taskDetailEntity.getTaskId());
        if (CollectionUtils.isNotEmpty(taskRelationEntityList)){
            //工作负责人填充
            CompletableFuture directorCompletableFuture = CompletableFuture.runAsync(() ->
                            taskInfoVO.setDirectors(userInfoService.getBaseUserVOByUserIdIn(taskRelationEntityList.stream()
                                    .filter(relation -> RelationType.DIRECTOR_USER.getType().equals(relation.getRelationType()))
                                    .map(TaskRelationEntity::getRelationId).collect(Collectors.toList())))
                    , Executors.newSingleThreadExecutor());


            //工作组成员填充
            CompletableFuture workerCompletableFuture = CompletableFuture.runAsync(() ->
                            taskInfoVO.setWorkers(userInfoService.getBaseUserVOByUserIdIn(taskRelationEntityList.stream()
                                    .filter(relation -> RelationType.WORKER_USER.getType().equals(relation.getRelationType()))
                                    .map(TaskRelationEntity::getRelationId).collect(Collectors.toList())))
                    , Executors.newSingleThreadExecutor());

            //设备列表填充
            CompletableFuture deviceCompletableFuture = CompletableFuture.runAsync(() ->
                            taskInfoVO.setDevices(deviceInfoService.getDeviceInfoByIdIn(taskRelationEntityList.stream()
                                    .filter(relation -> RelationType.DEVICE.getType().equals(relation.getRelationType()))
                                    .map(TaskRelationEntity::getRelationId).collect(Collectors.toList())))
                    , Executors.newSingleThreadExecutor());


            //站点填充
            CompletableFuture stationCompletableFuture = CompletableFuture.runAsync(() -> {
                        StationInfoVO stationInfoVO = deviceInfoService.getStationById(taskInfoVO.getStationId());
                        if (stationInfoVO != null) {
                            taskInfoVO.setStationName(stationInfoVO.getStationName());
                        }
                    }
                    , Executors.newSingleThreadExecutor());

            CompletableFuture.allOf(directorCompletableFuture, workerCompletableFuture, deviceCompletableFuture, stationCompletableFuture).join();
        }







        return taskInfoVO;
    }



}
