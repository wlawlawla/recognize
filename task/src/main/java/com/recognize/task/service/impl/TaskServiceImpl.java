package com.recognize.task.service.impl;

import ch.qos.logback.core.db.dialect.DBUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.constant.BaseConstants;
import com.recognize.common.service.IConstantService;
import com.recognize.common.util.DateUtils;
import com.recognize.common.util.JsonUtil;
import com.recognize.common.util.VOUtil;
import com.recognize.common.vo.ConstantVO;
import com.recognize.common.vo.PageVO;
import com.recognize.device.entity.StrapScreenEntity;
import com.recognize.device.service.IDeviceInfoService;
import com.recognize.device.vo.StationInfoVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.task.constant.RelationType;
import com.recognize.task.constant.TaskRecordType;
import com.recognize.task.dto.TaskInfoDto;
import com.recognize.task.entity.TaskDetailEntity;
import com.recognize.task.entity.TaskRecordEntity;
import com.recognize.task.entity.TaskRelationEntity;
import com.recognize.task.mapper.TaskDetailMapper;
import com.recognize.task.mapper.TaskRecordMapper;
import com.recognize.task.mapper.TaskRelationMapper;
import com.recognize.task.parameter.TaskSearchParameter;
import com.recognize.task.service.ITaskRelationService;
import com.recognize.task.service.ITaskService;
import com.recognize.task.vo.TaskInfoVO;
import com.recognize.user.service.IUserInfoService;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
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

    @Autowired
    private TaskRecordMapper taskRecordMapper;

    @Autowired
    private IConstantService constantService;

    private Map<String, String> statusCodeMap;
    private Map<String, String> statusValueMap;

    @PostConstruct
    private void init(){
        statusCodeMap = new HashMap<>();
        statusValueMap = new HashMap<>();

        List<ConstantVO> statusList = constantService.getConstantByType(BaseConstants.CONSTANT_INSPECTION_STATUS);
        if (CollectionUtils.isNotEmpty(statusList)){
            statusCodeMap.putAll(statusList.stream().collect(Collectors.toMap(ConstantVO::getCode, ConstantVO::getValue)));
            statusValueMap.putAll(statusList.stream().collect(Collectors.toMap(ConstantVO::getValue, ConstantVO::getCode)));
        }
    }

    @Override
    public TaskInfoVO saveTask(TaskInfoDto taskInfoDto, BaseUserVO currentUser){
        if (taskInfoDto == null){
            return null;
        }

        TaskDetailEntity taskDetailEntity = VOUtil.getVO(TaskDetailEntity.class, taskInfoDto);
        if (taskInfoDto.getTaskId() != null){
            TaskDetailEntity dbTaskDetail = taskDetailMapper.findByTaskId(taskInfoDto.getTaskId());
            if (dbTaskDetail != null){
                taskDetailEntity.setCreateTime(dbTaskDetail.getCreateTime());
                taskDetailEntity.setCreateBy(dbTaskDetail.getCreateBy());
                taskDetailEntity.setStartTime(dbTaskDetail.getStartTime());
                taskDetailEntity.setTaskNumber(dbTaskDetail.getTaskNumber());
                taskDetailEntity.setUpdateBy(currentUser.getUserId());
                taskDetailEntity.setUpdateTime(LocalDateTime.now());

                //status内容转换成code
                taskDetailEntity.setStatus(statusValueMap.get(taskInfoDto.getStatus()));

                taskDetailMapper.updateById(taskDetailEntity);
            }
            taskRelationMapper.deleteByTaskId(taskInfoDto.getTaskId());
        }else {
            if (StringUtils.isBlank(taskDetailEntity.getTaskName())){
                taskDetailEntity.setTaskName(DateUtils.getNumberStrDate(LocalDate.now()) + "_" + taskDetailEntity.getTaskType());
            }
            //首次创建任务，生成编号，并且任务状态默认为"巡检中"
            taskDetailEntity.setTaskNumber(DateUtils.NUMBER_DATE_TIME_FORMAT.format(LocalDateTime.now()));
            taskDetailEntity.setStatus(BaseConstants.CONSTANT_INSPECTION_STATUS_DOING);
            taskDetailEntity.setCreateBy(currentUser.getUserId());
            taskDetailEntity.setUpdateBy(currentUser.getUserId());
            taskDetailMapper.insert(taskDetailEntity);
        }

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

        TaskInfoVO taskInfoVO =  getTaskInfoVO(taskDetailEntity);

        saveTaskRecord(null, taskInfoVO.getTaskId(), TaskRecordType.TASK_INFO.getName(), taskInfoVO);

        return taskInfoVO;
    }

    @Override
    public TaskInfoVO getTaskInfo(Long taskId){
        if (taskId == null){
            return null;
        }

        TaskDetailEntity taskDetailEntity = taskDetailMapper.findByTaskId(taskId);

        if (taskDetailEntity == null){
            return null;
        }

        return getTaskInfoVO(taskDetailEntity);

    }

    /**
     * 保存任务记录信息
     * @param id
     * @param taskId
     * @param name
     * @param t
     * @param <T>
     */
    private <T> void saveTaskRecord(Long id, Long taskId, String name, T t){
        if (t == null){
            return;
        }
        CompletableFuture.runAsync(() -> {
            TaskRecordEntity taskRecordEntity = new TaskRecordEntity();
            taskRecordEntity.setTaskId(taskId);
            taskRecordEntity.setColumnName(name);
            taskRecordEntity.setContext(JsonUtil.toJsonString(t));

            if (id == null){
                taskRecordMapper.insert(taskRecordEntity);
            }else {
                taskRecordEntity.setId(id);
                taskRecordMapper.updateById(taskRecordEntity);
            }
        }, Executors.newSingleThreadExecutor());

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

        taskInfoVO.setStatus(statusCodeMap.get(taskInfoVO.getStatus()));
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

    /**
     * 搜索任务列表
     * @param pageable
     * @param searchParameter
     * @return
     */
    @Override
    public PageVO<TaskInfoVO> searchTask(Pageable pageable, TaskSearchParameter searchParameter){

        Page page = new Page();
        page.setCurrent(pageable.getPageNumber());
        page.setSize(pageable.getPageSize());

        PageVO<TaskInfoVO> taskInfoVOPageVO = new PageVO<>();
        Page<TaskDetailEntity> taskDetailEntityPage = taskDetailMapper.searchTask(page, searchParameter);
        taskInfoVOPageVO.setPage((int)taskDetailEntityPage.getCurrent());
        taskInfoVOPageVO.setSize((int)taskDetailEntityPage.getSize());
        taskInfoVOPageVO.setTotal(taskDetailEntityPage.getTotal());

        List<TaskInfoVO> taskInfoVOS = new ArrayList<>();
        taskInfoVOPageVO.setItems(taskInfoVOS);

        List<TaskDetailEntity> taskDetailEntityList = taskDetailEntityPage.getRecords();

        fullTaskVOList(taskInfoVOS, taskDetailEntityList);

        return taskInfoVOPageVO;
    }

    /**
     * 批量转换
     * @param taskInfoVOList
     * @param taskDetailEntityList
     */
    private void fullTaskVOList(List<TaskInfoVO> taskInfoVOList, List<TaskDetailEntity> taskDetailEntityList){
        if (CollectionUtils.isEmpty(taskDetailEntityList)){
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletableFuture[] completableFutures =
                taskDetailEntityList.stream().map(taskDetailEntity -> CompletableFuture.runAsync(() -> taskInfoVOList.add(getTaskInfoVO(taskDetailEntity)), executorService)).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        executorService.shutdown();

    }

    @Override
    public List<TaskInfoVO> getCurrentTaskList(BaseUserVO currentUser, Integer relationType){
        List<TaskInfoVO> taskInfoVOS = new ArrayList<>();
        List<TaskDetailEntity> currentTask = taskDetailMapper.getCurrentTask(currentUser.getUserId(), relationType);
        if (CollectionUtils.isNotEmpty(currentTask)){
            fullTaskVOList(taskInfoVOS, currentTask);
        }
        return taskInfoVOS;
    }


}
