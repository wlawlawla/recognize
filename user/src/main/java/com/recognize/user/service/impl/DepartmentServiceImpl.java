package com.recognize.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.constant.BaseConstants;
import com.recognize.common.exception.ResultErrorEnum;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.exception.ValidationError;
import com.recognize.common.util.VOUtil;
import com.recognize.user.entity.BaseDepartmentEntity;
import com.recognize.user.mapper.BaseDepartmentMapper;
import com.recognize.user.service.IDepartmentService;
import com.recognize.user.vo.BaseDepartmentVO;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@TranslationalService
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private BaseDepartmentMapper baseDepartmentMapper;

    @Override
    public BaseDepartmentVO addDepartment(BaseDepartmentVO baseDepartmentVO, BaseUserVO currentUser) {
        if (baseDepartmentVO == null) {
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

        BaseDepartmentEntity baseDepartmentEntity = VOUtil.getVO(BaseDepartmentEntity.class, baseDepartmentVO);

        //查询最大的排序数字
        Integer maxOrderNumber = baseDepartmentMapper.getMaxOrderNumber(baseDepartmentVO.getParentId());
        baseDepartmentEntity.setOrderNumber(maxOrderNumber + 1);

        baseDepartmentEntity.setUpdateBy(currentUser.getUserId());
        baseDepartmentEntity.setCreateBy(currentUser.getUserId());

        baseDepartmentMapper.insert(baseDepartmentEntity);

        return VOUtil.getVO(BaseDepartmentVO.class, baseDepartmentEntity);
    }


    @Override
    public List<BaseDepartmentVO> getDepartmentTree() {
        List<BaseDepartmentEntity> baseDepartmentEntityList = baseDepartmentMapper.findAll();
        if (CollectionUtils.isEmpty(baseDepartmentEntityList)) {
            return Collections.EMPTY_LIST;
        }

        List<BaseDepartmentVO> baseDepartmentVOList = VOUtil.getVOList(BaseDepartmentVO.class, baseDepartmentEntityList);

        Map<Long, BaseDepartmentVO> departmentVOMap = baseDepartmentVOList.stream().collect(Collectors.toMap(BaseDepartmentVO::getDepartmentId, department -> department));

        baseDepartmentVOList.forEach(department -> {
            if (department.getParentId() != null) {
                BaseDepartmentVO parent = departmentVOMap.get(department.getParentId());
                if (parent == null) {
                    return;
                }

                if (CollectionUtils.isEmpty(parent.getChildren())) {
                    parent.setChildren(new ArrayList<>());
                }

                parent.getChildren().add(department);

                //根据order排序
                if (parent.getChildren().size() > 1) {
                    parent.getChildren().sort(Comparator.comparing(BaseDepartmentVO::getOrderNumber));
                }
            }
        });

        return baseDepartmentVOList.stream().filter(department -> department.getParentId() == null).sorted(Comparator.comparing(BaseDepartmentVO::getOrderNumber)).collect(Collectors.toList());
    }

    @Override
    public String deleteDepartment(Long departmentId, BaseUserVO currentUser) {
        if (departmentId == null) {
            return null;
        }

        BaseDepartmentEntity baseDepartmentEntity = baseDepartmentMapper.findById(departmentId);
        if (baseDepartmentEntity == null || BaseConstants.BASE_ENTITY_INVALID.equals(baseDepartmentEntity.getDel())) {
            return null;
        }

        //逻辑删除部门：修改is_del=1
        // todo 暂时不对order_num做处理 待需求明确之后再调整
        baseDepartmentEntity.setDel(BaseConstants.BASE_ENTITY_INVALID);
        baseDepartmentEntity.setUpdateTime(LocalDateTime.now());
        baseDepartmentEntity.setUpdateBy(currentUser.getUserId());

        baseDepartmentMapper.updateById(baseDepartmentEntity);
        return baseDepartmentEntity.getDepartmentName();
    }

    @Override
    public BaseDepartmentVO updateDepartment(BaseDepartmentVO baseDepartmentVO, BaseUserVO currentUser) {
        if (baseDepartmentVO == null || baseDepartmentVO.getDepartmentId() == null) {
            throw new ResultErrorException(ResultErrorEnum.BAD_REQUEST);
        }

        BaseDepartmentEntity dbDepartmentEntity = baseDepartmentMapper.findById(baseDepartmentVO.getDepartmentId());
        if (dbDepartmentEntity == null || BaseConstants.BASE_ENTITY_INVALID.equals(dbDepartmentEntity.getDel())) {
            throw new ResultErrorException(ValidationError.setMissing("删除失败，部门不存在：departmentId = " + baseDepartmentVO.getDepartmentId()));
        }

        dbDepartmentEntity.setDepartmentName(baseDepartmentVO.getDepartmentName());
        //todo 暂不处理order_num  暂时不可迁移parentid 防止循环导致异常
        //dbDepartmentEntity.setParentId(baseDepartmentVO.getParentId());
        dbDepartmentEntity.setUpdateBy(currentUser.getUserId());
        dbDepartmentEntity.setUpdateTime(LocalDateTime.now());

        baseDepartmentMapper.updateById(dbDepartmentEntity);

        return VOUtil.getVO(BaseDepartmentVO.class, dbDepartmentEntity);
    }


}
