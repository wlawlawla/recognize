package com.recognize.device.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.recognize.device.vo.DeviceInfoVO;
import com.recognize.device.vo.StrapDetailVO;
import com.recognize.device.vo.StrapScreenVO;
import com.recognize.user.vo.BaseUserVO;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class DeviceImportUtil {

    private final static String strap_name = "压板名称";

    private final static String hard_row_num = "硬压板行数";
    private final static String hard_name_suffix = "_硬压板";
    private final static String hard_type = "硬压板";

    private final static String soft_page_num = "软压板页码";
    private final static String soft_type = "软压板";


    public static List<DeviceInfoVO> convertToVO(MultipartFile file) throws IOException {
        List<DeviceInfoVO> deviceInfoVOList = new ArrayList<>();


        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            try {
                Sheet sheet = sheetIterator.next();
                DeviceInfoVO deviceInfoVO = new DeviceInfoVO();

                Row row = sheet.getRow(1);
                Cell cell = row.getCell(1);
                cell.setCellType(CellType.STRING);
                deviceInfoVO.setDeviceName(cell.getStringCellValue());
                cell = row.getCell(3);
                cell.setCellType(CellType.STRING);
                deviceInfoVO.setDeviceRFID(cell.getStringCellValue());
                // todo 所属部门暂无
                // cell = row.getCell(5);

                cell = row.getCell(7);
                cell.setCellType(CellType.STRING);
                deviceInfoVO.setDeviceNum(cell.getStringCellValue());
                cell = row.getCell(9);
                cell.setCellType(CellType.STRING);
                deviceInfoVO.setStationName(cell.getStringCellValue());

                deviceInfoVO.setStrapScreenVOList(new ArrayList<>());
                StrapScreenVO currentScreen = null;

                int lastRowNum = sheet.getLastRowNum();

                for (int i = 2; i <= lastRowNum; i++){
                    row = sheet.getRow(i);
                    if (row == null || row.getLastCellNum() < 6){
                        continue;
                    }
                    cell = row.getCell(0);
                    cell.setCellType(CellType.STRING);
                    String value = cell.getStringCellValue();
                    if (StringUtils.isNotBlank(value)){
                        switch (value){
                            case hard_row_num:{
                                if (currentScreen != null){
                                    deviceInfoVO.getStrapScreenVOList().add(currentScreen);
                                }
                                currentScreen = new StrapScreenVO();
                                currentScreen.setScreenTypeStr(hard_type);
                                currentScreen.setScreenName(deviceInfoVO.getDeviceName() + hard_name_suffix);
                                cell = row.getCell(1);
                                cell.setCellType(CellType.STRING);
                                currentScreen.setRowNumber(Integer.valueOf(cell.getStringCellValue()));
                                cell = row.getCell(3);
                                cell.setCellType(CellType.STRING);
                                currentScreen.setColumnNumber(Integer.valueOf(cell.getStringCellValue()));
                                cell = row.getCell(5);
                                cell.setCellType(CellType.STRING);
                                currentScreen.setStrapNumber(Integer.valueOf(cell.getStringCellValue()));
                            }
                            break;

                            case strap_name:{
                                if (currentScreen.getStrapDetailVOList() == null){
                                    currentScreen.setStrapDetailVOList(new ArrayList<>());
                                }
                                StrapDetailVO strapDetailVO = new StrapDetailVO();
                                cell = row.getCell(1);
                                cell.setCellType(CellType.STRING);
                                strapDetailVO.setStrapName(cell.getStringCellValue());
                                cell = row.getCell(3);
                                cell.setCellType(CellType.STRING);
                                strapDetailVO.setStrapValue(cell.getStringCellValue());
                                cell = row.getCell(5);
                                cell.setCellType(CellType.STRING);
                                strapDetailVO.setStrapPosition(Integer.valueOf(cell.getStringCellValue()));
                                currentScreen.getStrapDetailVOList().add(strapDetailVO);
                            }
                            break;

                            case soft_page_num:{
                                if (currentScreen != null){
                                    deviceInfoVO.getStrapScreenVOList().add(currentScreen);
                                }
                                currentScreen = new StrapScreenVO();
                                currentScreen.setScreenTypeStr(soft_type);

                                cell = row.getCell(1);
                                cell.setCellType(CellType.STRING);
                                currentScreen.setSoftPage(Integer.valueOf(cell.getStringCellValue()));
                                cell = row.getCell(3);
                                cell.setCellType(CellType.STRING);
                                currentScreen.setScreenName(cell.getStringCellValue());
                                cell = row.getCell(5);
                                cell.setCellType(CellType.STRING);
                                currentScreen.setSoftTypeStr(cell.getStringCellValue());

                            }
                            break;

                        }

                    }
                }
                if (currentScreen != null){
                    deviceInfoVO.getStrapScreenVOList().add(currentScreen);
                }

                if (CollectionUtils.isNotEmpty(deviceInfoVO.getStrapScreenVOList())){
                    deviceInfoVO.getStrapScreenVOList().forEach(strapScreenVO -> {
                        strapScreenVO.setDeviceName(deviceInfoVO.getDeviceName());
                        if (strapScreenVO.getStrapNumber() == null){
                            if (CollectionUtils.isNotEmpty(strapScreenVO.getStrapDetailVOList())){
                                strapScreenVO.setStrapNumber(strapScreenVO.getStrapDetailVOList().size());
                            }
                        }
                    });
                }
                deviceInfoVOList.add(deviceInfoVO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return deviceInfoVOList;
    }


}
