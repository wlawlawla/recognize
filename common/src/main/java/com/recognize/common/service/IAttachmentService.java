package com.recognize.common.service;

import com.recognize.common.vo.AttachmentVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface IAttachmentService {

    /**
     * 下载附件
     * @param attachmentId
     * @param response
     */
    void downloadImageById(Long attachmentId, HttpServletResponse response);

    /**
     * 下载附件
     * @param fkSid
     * @param type
     * @param response
     */
    void downloadImageByTypeAndFkSid(Long fkSid, Integer type, HttpServletResponse response);


    /**
     * 上传文件
     * @param file
     * @param type
     * @param fId
     * @return
     */
    AttachmentVO uploadAttachment(MultipartFile file, Integer type, Long fId);

    /**
     * 保存文件
     * @param file
     * @param type
     * @param fId
     * @return
     */
    AttachmentVO uploadAttachment(File file, Integer type, Long fId);

    /**
     * 删除附件
     * @param id
     */
    void deleteById(Long id);
}
