package com.recognize.common.service;

import com.recognize.common.vo.ConstantVO;

import java.util.List;

public interface IConstantService {

    /**
     * 重新查询数据库常量 存入内存
     */
    void refresh();

    /**
     * 根据类型获取常量列表
     * @param type
     * @return
     */
    List<ConstantVO> getConstantByType(String type);

}
