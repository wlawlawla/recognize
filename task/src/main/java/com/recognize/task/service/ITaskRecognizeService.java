package com.recognize.task.service;

import com.recognize.task.vo.TaskRecognizeVO;
import com.recognize.user.vo.BaseUserVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ITaskRecognizeService {

    /**
     * 识别接口
     * @param taskId
     * @param screenId
     * @param file
     * @param currentUser
     * @return
     * @throws IOException
     */
    TaskRecognizeVO analysisScreenImage(Long taskId, Long screenId, MultipartFile file, BaseUserVO currentUser) throws IOException;
}
