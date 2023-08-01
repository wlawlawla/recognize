package com.recognize.common.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    public static File getFile(Object object, String filePath, String fileName) throws IOException {
        File file = new File(fileName);
        InputStream inputStream = object.getClass().getClassLoader().getResourceAsStream(filePath);
        FileUtils.copyInputStreamToFile(inputStream, file);
        return file;
    }
}
