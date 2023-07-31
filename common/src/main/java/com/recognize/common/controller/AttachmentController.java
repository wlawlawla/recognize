package com.recognize.common.controller;

import com.recognize.common.service.IAttachmentService;
import com.recognize.common.vo.AttachmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    private IAttachmentService attachmentService;

    /**
     * 上传文件
     * @param file
     * @param type
     * @param fid
     * @return
     */
    @PostMapping
    public ResponseEntity<AttachmentVO> uploadAttachment(@RequestParam("file") MultipartFile file,
                                                         @RequestParam(value = "type", required = false) Integer type,
                                                         @RequestParam(value = "fid", required = false) Long fid) {

        return new ResponseEntity<>(attachmentService.uploadAttachment(file, type, fid), HttpStatus.OK);
    }

    /**
     * 下载文件
     * @param attachmentId
     * @param response
     */
    @GetMapping("/{attachmentId}")
    public void downloadAttachment(@PathVariable Long attachmentId, HttpServletResponse response){
        attachmentService.downloadAttachmentById(attachmentId, response);
    }



}
