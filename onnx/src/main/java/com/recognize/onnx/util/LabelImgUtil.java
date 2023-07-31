package com.recognize.onnx.util;

import com.recognize.onnx.yolo.Detection;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LabelImgUtil {

    private static String folder = "test";

    private static String fileName = "replace_name";
    private static String label_name = "replace_label";
    private static String path = "replace_path";
    private static String xmin = "replace_xmin";
    private static String ymin = "replace_ymin";
    private static String xmax = "replace_xmax";
    private static String ymax = "replace_ymax";


    private static String header = "<annotation>\n" +
            "\t<folder>" + folder + "</folder>\n" +
            "\t<filename>replace_name</filename>\n" +
            "\t<path>replace_path</path>\n" +
            "\t<source>\n" +
            "\t\t<database>Unknown</database>\n" +
            "\t</source>\n" +
            "\t<size>\n" +
            "\t\t<width>4000</width>\n" +
            "\t\t<height>3000</height>\n" +
            "\t\t<depth>3</depth>\n" +
            "\t</size>\n" +
            "\t<segmented>0</segmented>";


    private static String end = "</annotation>";


    private static String context = "\t<object>\n" +
            "\t\t<name>replace_label</name>\n" +
            "\t\t<pose>Unspecified</pose>\n" +
            "\t\t<truncated>0</truncated>\n" +
            "\t\t<difficult>0</difficult>\n" +
            "\t\t<bndbox>\n" +
            "\t\t\t<xmin>replace_xmin</xmin>\n" +
            "\t\t\t<ymin>replace_ymin</ymin>\n" +
            "\t\t\t<xmax>replace_xmax</xmax>\n" +
            "\t\t\t<ymax>replace_ymax</ymax>\n" +
            "\t\t</bndbox>\n" +
            "\t</object>";


    public static String setLabelInfo(List<Detection> detectionList, File file) {
        if (CollectionUtils.isEmpty(detectionList) || !file.exists()){
            return "";
        }

        StringBuffer infoBuf = new StringBuffer();
        try {
            String headInfo = header
                    .replace(fileName, file.getName())
                    .replace(path, file.getCanonicalPath());
            infoBuf.append(headInfo);
        } catch (IOException e){
            e.printStackTrace();
        }

        detectionList.forEach(detection -> {
            String content = context
                    .replace(label_name, detection.getLabel() + "")
                    .replace(xmin, detection.getBbox()[0] + "")
                    .replace(ymin, detection.getBbox()[1] + "")
                    .replace(xmax, detection.getBbox()[2] + "")
                    .replace(ymax, detection.getBbox()[3] + "");
            infoBuf.append(content);
        });
        infoBuf.append(end);
        return infoBuf.toString();

    }
}
