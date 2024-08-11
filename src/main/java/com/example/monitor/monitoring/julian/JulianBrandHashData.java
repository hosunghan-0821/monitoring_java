package com.example.monitor.monitoring.julian;


import com.example.monitor.monitoring.gebnegozi.GebenegoziSaleInfoString;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import java.util.stream.Collectors;

import static com.example.monitor.monitoring.julian.JulianFindString.JULIAN_MONITORING_SITE;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.FALL_WINTER_2024_2025;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.SPRING_SUMMER_2024;


@Slf4j
@Component
public class JulianBrandHashData {


    private final HashMap<String, HashMap<String, JulianProduct>> julianHashMap;

    @Getter
    private final HashMap<String, JulianSaleInfo> julianSaleInfoHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public JulianBrandHashData() {

        julianSaleInfoHashMap = new HashMap<>();
        julianHashMap = new HashMap<>();

        for (String brandName : JULIAN_MONITORING_SITE) {
            julianHashMap.put(brandName, new HashMap<>());
        }

        productKeySet = new HashSet<>();
    }

    public HashMap<String, JulianProduct> getBrandHashMap(String brandName) {
        return julianHashMap.get(brandName);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initJulianData() {
        List<File> xlsxFiles = findXlsxFiles();

        for (File file : xlsxFiles) {
            String fileName = file.getName();
            log.info(fileName + " sales info 데이터 로드 시작");
            String season = fileName.split("\\.")[1].toUpperCase();
            season = changeSeasonToWebSignature(season);
            try (
                    Workbook workbook = WorkbookFactory.create(file);
            ) {

                Sheet sheet = workbook.getSheetAt(0);
                int startIndex = 4;
                int lastIndex = sheet.getLastRowNum();
                for (int i = startIndex; i <= lastIndex; i++) {
                    Row row = sheet.getRow(i);

                    //brand이름이 비어있으면 패스
                    if (getCellValue(row.getCell(0)).isEmpty()) {
                        continue;
                    }
                    String brandName = getCellValue(row.getCell(0));


                    String woman_clothes = getCellValue(row.getCell(JulianSaleInfoString.WOMAN_CLOTHES_COLUMN_INDEX));
                    String woman_shoes = getCellValue(row.getCell(JulianSaleInfoString.WOMAN_SHOES_COLUMN_INDEX));
                    String woman_bags = getCellValue(row.getCell(JulianSaleInfoString.WOMAN_BAGS_COLUMN_INDEX));
                    String woman_acc = getCellValue(row.getCell(JulianSaleInfoString.WOMAN_ACC_COLUMN_INDEX));

                    String man_clothes = getCellValue(row.getCell(JulianSaleInfoString.MAN_CLOTHES_COLUMN_INDEX));
                    String man_shoes = getCellValue(row.getCell(JulianSaleInfoString.MAN_SHOES_COLUMN_INDEX));
                    String man_bags = getCellValue(row.getCell(JulianSaleInfoString.MAN_BAGS_COLUMN_INDEX));
                    String man_acc = getCellValue(row.getCell(JulianSaleInfoString.MAN_ACC_COLUMN_INDEX));

                    //log.info("{}, \t// {} ,{}, {}, {}, \t// {}, {}, {}, {}", brandName, woman_clothes, woman_shoes, woman_bags, woman_acc, man_clothes, man_shoes, man_bags, man_acc);

                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "CLOTHING", "WOMAN"), getJulianSaleInfo(season, woman_clothes, brandName, "CLOTHING"));
                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "SHOES", "WOMAN"), getJulianSaleInfo(season, woman_shoes, brandName, "SHOES"));
                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "BAGS", "WOMAN"), getJulianSaleInfo(season, woman_bags, brandName, "BAGS"));
                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "ACCESSORIES", "WOMAN"), getJulianSaleInfo(season, woman_acc, brandName, "ACCESSORIES"));

                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "CLOTHING", "MAN"), getJulianSaleInfo(season, man_clothes, brandName, "CLOTHING"));
                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "SHOES", "MAN"), getJulianSaleInfo(season, man_shoes, brandName, "SHOES"));
                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "BAGS", "MAN"), getJulianSaleInfo(season, man_bags, brandName, "BAGS"));
                    julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "ACCESSORIES", "MAN"), getJulianSaleInfo(season, man_acc, brandName, "ACCESSORIES"));

                }
                log.info(fileName + " sales info 데이터 로드 완료");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("액셀 데이터 로드 오류");
            }
        }
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

    private JulianSaleInfo getJulianSaleInfo(String season, String categorySalesPercent, String brandName, String category) {


        if (categorySalesPercent.isBlank()) {
            categorySalesPercent = "0";
        }
        return JulianSaleInfo.builder()
                .brandName(brandName)
                .season(season)
                .salesPercent(Integer.parseInt(categorySalesPercent))
                .category(category)
                .build();
    }

    private String changeSeasonToWebSignature(String season) {
        switch (season) {
            case "FW24-25":
                return FALL_WINTER_2024_2025;
            case "SS24":
                return SPRING_SUMMER_2024;
            default:
                assert (false) : "cannot come here";
                return "Error";
        }
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
                    if (fileName.endsWith(".xlsx") && fileName.contains("JULIAN")) {
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
