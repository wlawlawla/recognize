package com.recognize.common.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.recognize.common.common.AttachmentType;
import com.recognize.common.common.TranslationalService;
import com.recognize.common.entity.BaseAttachmentEntity;
import com.recognize.common.exception.ResultErrorException;
import com.recognize.common.exception.ValidationError;
import com.recognize.common.mapper.BaseAttachmentMapper;
import com.recognize.common.service.IAttachmentService;
import com.recognize.common.util.VOUtil;
import com.recognize.common.vo.AttachmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

@TranslationalService
public class AttachmentServiceImpl implements IAttachmentService {

    @Autowired
    private BaseAttachmentMapper baseAttachmentMapper;

    @Override
    public void downloadImageById(Long attachmentId, HttpServletResponse response){
        if (attachmentId == null){
            return;
        }
        downloadImage(baseAttachmentMapper.findById(attachmentId), response);
    }

    /**
     * 下载图片
     * @param baseAttachmentEntity
     * @param response
     */
    private void downloadImage(BaseAttachmentEntity baseAttachmentEntity, HttpServletResponse response){
        if (baseAttachmentEntity == null){
            return;
        }
        try {
            response.setContentType("image/png");
            response.setHeader("Content-disposition", "attachment; filename=" + baseAttachmentEntity.getAttachmentName());
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            OutputStream out = response.getOutputStream();
            out.write(baseAttachmentEntity.getContent());
        } catch (IOException e) {
            throw new ResultErrorException(ValidationError.setInvalid("下载附件异常"));
        }

    }

    @Override
    public void downloadImageByTypeAndFkSid(Long fkSid, Integer type, HttpServletResponse response){
        if (fkSid == null || type == null){
            return;
        }
        BaseAttachmentEntity baseAttachmentEntity = baseAttachmentMapper.findByFkSidAndType(fkSid, type);

        downloadImage(baseAttachmentEntity, response);

    }


    @Override
    public AttachmentVO uploadAttachment(MultipartFile file, Integer type, Long fId){
        if (file == null){
            return null;
        }

        BaseAttachmentEntity baseAttachmentEntity = new BaseAttachmentEntity();
        String name = file.getOriginalFilename();
        Double size = BigDecimal.valueOf((file.getSize() * 1.00) / (1024 * 1024)).setScale(4, RoundingMode.HALF_UP).doubleValue();
        try {
            baseAttachmentEntity.setContent(file.getBytes());
        } catch (IOException e) {
            throw new ResultErrorException(ValidationError.setInvalid("上传图片失败"));
        }

        baseAttachmentEntity.setAttachmentName(name);
        baseAttachmentEntity.setAttachmentType(type == null ? AttachmentType.DEFAULT.getType() : type);
        baseAttachmentEntity.setSizeInMb(size);
        baseAttachmentEntity.setForeignKeySid(fId);

        baseAttachmentMapper.insert(baseAttachmentEntity);
        return VOUtil.getVO(AttachmentVO.class, baseAttachmentEntity);
    }

    @Override
    public AttachmentVO uploadAttachment(File file, Integer type, Long fId){
        if (file == null){
            return null;
        }

        BaseAttachmentEntity baseAttachmentEntity = new BaseAttachmentEntity();
        String name = file.getName();
        Double size = BigDecimal.valueOf((FileUtil.size(file) * 1.00) / (1024 * 1024)).setScale(4, RoundingMode.HALF_UP).doubleValue();
        try {
            baseAttachmentEntity.setContent(FileUtil.readBytes(file));
        } catch (IORuntimeException e) {
            throw new ResultErrorException(ValidationError.setInvalid("上传图片失败"));
        }

        baseAttachmentEntity.setAttachmentName(name);
        baseAttachmentEntity.setAttachmentType(type == null ? AttachmentType.DEFAULT.getType() : type);
        baseAttachmentEntity.setSizeInMb(size);
        baseAttachmentEntity.setForeignKeySid(fId);

        baseAttachmentMapper.insert(baseAttachmentEntity);
        return VOUtil.getVO(AttachmentVO.class, baseAttachmentEntity);
    }

    @Override
    public void deleteById(Long id){
        if (id == null){
            return;
        }
        baseAttachmentMapper.deleteByAttachmentId(id);
    }

}
