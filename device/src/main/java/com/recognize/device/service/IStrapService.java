package com.recognize.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.recognize.common.vo.PageVO;
import com.recognize.device.entity.StrapDetailEntity;
import com.recognize.device.parameter.ScreenSearchParameter;
import com.recognize.device.parameter.StrapSearchParameter;
import com.recognize.device.vo.StrapDetailVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IStrapService extends IService<StrapDetailEntity> {

    /**
     * 添加压板屏幕
     * @param strapScreenVO
     * @param currentUser
     * @return
     */
    StrapScreenVO addStrapScreen(StrapScreenVO strapScreenVO, BaseUserVO currentUser);

    /**
     * 添加压板
     * @param strapDetailVO
     * @param currentUser
     * @return
     */
    StrapDetailVO addStrapDetail(StrapDetailVO strapDetailVO, BaseUserVO currentUser);

    /**
     * 根据压板屏幕id获取压板列表
     * @param screenId
     * @return
     */
    List<StrapDetailVO> getStrapDetailByScreenId(Long screenId);

    /**
     * 根据设备id获取压板屏幕列表
     * @param deviceId
     * @return
     */
    List<StrapScreenVO> getStrapScreenListByDeviceId(Long deviceId);

    /**
     * 获取压板屏信息 包含压板列表
     * @param screenId
     * @return
     */
    StrapScreenVO getStrapScreenInfo(Long screenId);

    /**
     * 更新压板信息
     * @param strapDetailVO
     * @param currentUser
     * @return
     */
    StrapDetailVO updateStrapDetail(StrapDetailVO strapDetailVO, BaseUserVO currentUser);

    /**
     * 更新压板屏信息 不包含图片
     * @param strapScreenVO
     * @param currentUser
     * @return
     */
    StrapScreenVO updateScreen(StrapScreenVO strapScreenVO, BaseUserVO currentUser);

    /**
     * 根据设备id删除压板屏幕 --逻辑删除
     * @param deviceId
     * @param currentUser
     */
    void deleteStrapScreenByDeviceId(Long deviceId, BaseUserVO currentUser);

    /**
     * 删除压板屏幕
     * @param screenId
     * @param currentUser
     */
    void deleteScreenById(Long screenId, BaseUserVO currentUser);

    /**
     * 删除压板
     * @param strapDetailId
     * @param currentUser
     */
    void deleteStrapDetailById(Long strapDetailId, BaseUserVO currentUser);

    /**
     * 获取图片
     * @param fkSid
     * @param type
     * @param response
     */
    void getImage(Long fkSid, Integer type, HttpServletResponse response);

    /**
     * 上传压板屏幕图片
     * @param screenId
     * @param file
     */
    void uploadScreenImage(Long screenId, MultipartFile file);

    /**
     * 搜索压板屏幕
     * @param pageable
     * @param searchParameter
     * @return
     */
    PageVO<StrapScreenVO> searchScreen(Pageable pageable, ScreenSearchParameter searchParameter);

    /**
     * 搜索压板
     * @param pageable
     * @param searchParameter
     * @return
     */
    PageVO<StrapDetailVO> searchStap(Pageable pageable, StrapSearchParameter searchParameter);
}
