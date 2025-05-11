package com.example.monitor.monitoring.gebnegozi;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.*;


@Slf4j
@Component
public class GebenegoziBrandHashData {


    @Getter
    private final HashMap<String, GebenegoziSaleInfo> gebenegoziSaleMap;

    private final HashMap<String, HashMap<String, GebenegoziProduct>> gebenegoziHashMap;

    private final List<String> brandNameList;
    @Getter
    private final HashSet<String> productKeySet;

    public GebenegoziBrandHashData() {

        gebenegoziSaleMap = new HashMap<>();
        gebenegoziHashMap = new HashMap<>();
        productKeySet = new HashSet<>();
        brandNameList = new ArrayList<>();
        for (String[] list : GEBE_URL_LIST) {
            String key = list[2];
            String brandName = list[0];
            brandNameList.add(brandName);
            gebenegoziHashMap.put(key, new HashMap<>());
        }

    }

    public Map<String, GebenegoziProduct> getBrandHashMap(String key) {
        return gebenegoziHashMap.get(key);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initGebeneData() {

        MDC.put("threadName", GNB);
        List<File> xlsxFiles = findXlsxFiles();

        for (File file : xlsxFiles) {
            String fileName = file.getName();
            log.info(fileName + " sales info 데이터 로드 시작");
            String season = fileName.split("\\.")[1].toUpperCase();

            try (
                    Workbook workbook = WorkbookFactory.create(file);
            ) {

                Sheet sheet = workbook.getSheetAt(0);
                int startIndex = 1;
                int lastIndex = sheet.getLastRowNum();
                for (int i = startIndex; i <= lastIndex; i++) {
                    Row row = sheet.getRow(i);

                    //brand이름이 비어있으면 패스
                    if (getCellValue(row.getCell(0)).isEmpty()) {
                        continue;
                    }

                    String brandName = getCellValue(row.getCell(0));
                    //브랜드 검사
                    if (!validateBrandName(brandName)) {
                        continue;
                    }

                    String woman_clothes = getCellValue(row.getCell(GebenegoziSaleInfoString.WOMAN_CLOTHES_COLUMN_INDEX));
                    String woman_shoes = getCellValue(row.getCell(GebenegoziSaleInfoString.WOMAN_SHOES_COLUMN_INDEX));
                    String woman_bags = getCellValue(row.getCell(GebenegoziSaleInfoString.WOMAN_BAGS_COLUMN_INDEX));
                    String woman_acc = getCellValue(row.getCell(GebenegoziSaleInfoString.WOMAN_ACC_COLUMN_INDEX));

                    String man_clothes = getCellValue(row.getCell(GebenegoziSaleInfoString.MAN_CLOTHES_COLUMN_INDEX));
                    String man_shoes = getCellValue(row.getCell(GebenegoziSaleInfoString.MAN_SHOES_COLUMN_INDEX));
                    String man_bags = getCellValue(row.getCell(GebenegoziSaleInfoString.MAN_BAGS_COLUMN_INDEX));
                    String man_acc = getCellValue(row.getCell(GebenegoziSaleInfoString.MAN_ACC_COLUMN_INDEX));

                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "CLOTHING", "woman"), getGebenegoziSaleInfo(season, woman_clothes, brandName, "CLOTHING", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.WOMAN_CLOTHES_COLUMN_INDEX))));
                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "SHOES", "woman"), getGebenegoziSaleInfo(season, woman_shoes, brandName, "SHOES", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.WOMAN_SHOES_COLUMN_INDEX))));
                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "BAGS", "woman"), getGebenegoziSaleInfo(season, woman_bags, brandName, "BAGS", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.WOMAN_BAGS_COLUMN_INDEX))));
                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "ACCESSORIES", "woman"), getGebenegoziSaleInfo(season, woman_acc, brandName, "ACCESSORIES", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.WOMAN_ACC_COLUMN_INDEX))));

                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "CLOTHING", "man"), getGebenegoziSaleInfo(season, man_clothes, brandName, "CLOTHING", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.MAN_CLOTHES_COLUMN_INDEX))));
                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "SHOES", "man"), getGebenegoziSaleInfo(season, man_shoes, brandName, "SHOES", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.MAN_SHOES_COLUMN_INDEX))));
                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "BAGS", "man"), getGebenegoziSaleInfo(season, man_bags, brandName, "BAGS", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.MAN_BAGS_COLUMN_INDEX))));
                    gebenegoziSaleMap.put(makeSalesInfoKey(brandName, season, "ACCESSORIES", "man"), getGebenegoziSaleInfo(season, man_acc, brandName, "ACCESSORIES", isCellFilledWithColor(row.getCell(GebenegoziSaleInfoString.MAN_ACC_COLUMN_INDEX))));
                }
                log.info(fileName + " sales info 데이터 로드 완료");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("액셀 데이터 로드 오류");
            }

        }
        MDC.clear();
    }


    public String makeSalesInfoKey(String brandName, String season, String category, String sex) {
        //unisex일 경우 woman으로 가격계산을 함.
        if (sex.equals("unisex")) {
            sex = "woman";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(brandName);
        sb.append("_");
        sb.append(category);
        sb.append("_");
        sb.append(season);
        sb.append("_");
        sb.append(sex);
        return sb.toString();
    }

    private boolean validateBrandName(String brandName) {

        return brandNameList.contains(brandName);

    }

    private boolean isCellFilledWithColor(Cell cell) {


        short foregroundColorIndex = cell.getCellStyle().getFillForegroundColor();
        Color foregroundColor = cell.getCellStyle().getFillForegroundColorColor();

        if (foregroundColorIndex != IndexedColors.AUTOMATIC.getIndex() && foregroundColor != null) {
            return true;
        } else {
            return false;
        }


    }

    private GebenegoziSaleInfo getGebenegoziSaleInfo(String season, String categorySalesPercent, String brandName, String category, boolean isColored) {
        if (categorySalesPercent.isBlank()) {
            categorySalesPercent = "0";
            isColored = false;
        }
        return GebenegoziSaleInfo.builder().season(season).category(category).salesPercent(Integer.parseInt(categorySalesPercent)).isColored(isColored).brandName(brandName).build();
    }


    private List<File> findXlsxFiles() {
        Path startPath = Paths.get(System.getProperty("user.dir"));

        List<Path> xlsxFiles = new ArrayList<>();
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // .xlsx 확장자를 가진 파일을 찾습니다.
                    String fileName = file.toString();
                    if (fileName.endsWith(".xlsx") && fileName.contains("GNB")) {
                        xlsxFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xlsxFiles.stream().map(Path::toFile).collect(Collectors.toList());
    }


    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return ""; // 셀이 비어있을 경우 빈 문자열 반환
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return ""; // 다른 타입의 셀은 빈 문자열 반환
        }
    }

}
