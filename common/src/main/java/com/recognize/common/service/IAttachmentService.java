package com.recognize.common.service;

import com.recognize.common.vo.AttachmentVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface IAttachmentService {

    /**
     * 下载附件
     * @param attachmentId
     * @param response
     */
    void downloadAttachmentById(Long attachmentId, HttpServletResponse response);


    /**
     * 上传文件
     * @param file
     * @param type
     * @param fId
     * @return
     */
    AttachmentVO uploadAttachment(MultipartFile file, Integer type, Long fId);
}
