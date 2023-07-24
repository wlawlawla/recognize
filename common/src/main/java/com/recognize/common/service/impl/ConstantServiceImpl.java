package com.recognize.common.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.entity.BaseConstantEntity;
import com.recognize.common.mapper.BaseConstantMapper;
import com.recognize.common.service.IConstantService;
import com.recognize.common.util.VOUtil;
import com.recognize.common.vo.ConstantVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@TranslationalService
public class ConstantServiceImpl implements IConstantService {

    @Autowired
    private BaseConstantMapper baseConstantMapper;

    private Map<String, List<ConstantVO>> constantMap = new HashMap<>(16);

    @PostConstruct
    @Override
    public void refresh(){
        constantMap.clear();
        List<BaseConstantEntity> baseConstantEntityList = baseConstantMapper.findAll();
        if (CollectionUtils.isEmpty(baseConstantEntityList)){
            return;
        }

        List<ConstantVO> constantVOList = VOUtil.getVOList(ConstantVO.class, baseConstantEntityList);

        constantMap.putAll(constantVOList.stream().collect(Collectors.groupingBy(ConstantVO::getType)));
    }

    @Override
    public List<ConstantVO> getConstantByType(String type){
        return constantMap.get(type);
    }










}
