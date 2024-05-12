package com.example.monitor.monitoring.gebnegozi;



import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.*;


@Slf4j
@Component
public class GebenegoziBrandHashData {

    private final HashMap<String, HashMap<String, GebenegoziProduct>> gebenegoziHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public GebenegoziBrandHashData() {

        gebenegoziHashMap = new HashMap<>();
        productKeySet = new HashSet<>();
        for (String[] list:GEBE_URL_LIST){
            String key = list[2];
            gebenegoziHashMap.put(key,new HashMap<>());
        }
    }

    public Map<String, GebenegoziProduct> getBrandHashMap(String key) {
        return gebenegoziHashMap.get(key);
    }

//    @PostConstruct
//    public void initGebeneData() {
//        File file = new File("data.xlsx");
//
//        try (
//                Workbook workbook = WorkbookFactory.create(file);
//        ) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//            int startIndex = 1;
//            int lastIndex = sheet.getLastRowNum();
//            boolean isGetHeaderInfo = false;
//            for (int i = startIndex; i <= lastIndex; i++) {
//                Row row = sheet.getRow(i);
//
//                if (getCellValue(row.getCell(0)).isEmpty()) {
//                    continue;
//                } else if (!isGetHeaderInfo) {
//                    short lastCellNum = row.getLastCellNum();
//                    for (int j = 0; j < lastCellNum; j++) {
//
//                        log.info(getCellValue(row.getCell(j)));
//                        log.info(row.getCell(j).getCellStyle().getFillForegroundColor()+"");;
//                    }
//                    isGetHeaderInfo = true;
//                }
//
//
//                String data1 = getCellValue(row.getCell(0));
//                String data2 = getCellValue(row.getCell(1));
//                String data3 = getCellValue(row.getCell(2));
//
////                log.info("{}, \t {}, \t{},\t", data1, data2, data3);
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("액셀 데이터 로드 오류");
//            return;
//        }
//    }


//    private static String getCellValue(Cell cell) {
//        if (cell == null) {
//            return ""; // 셀이 비어있을 경우 빈 문자열 반환
//        }
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue();
//            case NUMERIC:
//                return String.valueOf((int) cell.getNumericCellValue());
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                return cell.getCellFormula();
//            default:
//                return ""; // 다른 타입의 셀은 빈 문자열 반환
//        }
//    }

}
