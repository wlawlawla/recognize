package com.recognize.device.util;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;

public class DeviceImageUtil {

    public static QrConfig getQrConfig(){
        QrConfig config = new QrConfig();
        // 高纠错级别
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        return config;
    }

    public static void generateImage(String message, File file){
        QrCodeUtil.generate(message, getQrConfig(), file);
    }
}
