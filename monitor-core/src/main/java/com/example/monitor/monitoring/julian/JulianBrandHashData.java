package com.example.monitor.monitoring.julian;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import java.util.stream.Collectors;

import static com.example.monitor.monitoring.julian.JulianFindString.JULIAN;
import static com.example.monitor.monitoring.julian.JulianFindString.JULIAN_MONITORING_SITE;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.FALL_WINTER_2024_2025;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.FALL_WINTER_2025_2026;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.OUTLET;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.SALE;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.SPRING_SUMMER_2024;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.SPRING_SUMMER_2025;


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
        MDC.put("threadName", JULIAN);
        for (File file : xlsxFiles) {
            String fileName = file.getName();
            log.info(fileName + " sales info 데이터 로드 시작");
            String season = fileName.split("\\.")[1].toUpperCase();
            season = changeSeasonToWebSignature(season);
            try (
                    Workbook workbook = WorkbookFactory.create(file);
            ) {

                Sheet sheet = workbook.getSheetAt(0);
                int startIndex = 2;
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

                    String unisex_clothes = "";
                    String unisex_shoes = "";
                    String unisex_bags = "";
                    String unisex_acc = "";

                    //TO-DO UNISEX 처리
                    String clothesResult = getHighPercentSexOrNull(woman_clothes, man_clothes);
                    if (clothesResult != null) {
                        unisex_clothes = clothesResult;
                        julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "CLOTHING", "UNISEX"), getJulianSaleInfo(season, unisex_clothes, brandName, "CLOTHING"));
                    }

                    String shoesResult = getHighPercentSexOrNull(woman_shoes, man_shoes);
                    if (shoesResult != null) {
                        unisex_shoes = shoesResult;
                        julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "SHOES", "UNISEX"), getJulianSaleInfo(season, unisex_shoes, brandName, "SHOES"));
                    }

                    String bagsResult = getHighPercentSexOrNull(woman_bags, man_bags);
                    if (bagsResult != null) {
                        unisex_bags = bagsResult;
                        julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "BAGS", "UNISEX"), getJulianSaleInfo(season, unisex_bags, brandName, "BAGS"));
                    }
                    String accResult = getHighPercentSexOrNull(woman_acc, man_acc);

                    if (accResult != null) {
                        unisex_acc = accResult;
                        julianSaleInfoHashMap.put(makeSalesInfoKey(brandName, season, "ACCESSORIES", "UNISEX"), getJulianSaleInfo(season, unisex_acc, brandName, "ACCESSORIES"));
                    }


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
        MDC.clear();
//        System.out.println(julianSaleInfoHashMap);
    }

    private String getHighPercentSexOrNull(String woman_category, String man_category) {
        String unisex_clothes = null;

        if (!woman_category.isBlank() && !man_category.isBlank()) {

            woman_category = woman_category.replaceAll(" ", "");
            man_category = man_category.replaceAll(" ", "");
            int woman_category_int = 0;
            int man_category_int = 0;
            try {
                woman_category_int = Integer.parseInt(woman_category);
            } catch (Exception e) {
            }
            try {
                man_category_int = Integer.parseInt(man_category);
            } catch (Exception e) {
            }


            if (woman_category_int > man_category_int) {
                unisex_clothes = woman_category;
            } else {
                unisex_clothes = man_category;
            }
        } else if (!woman_category.isBlank()) {
            unisex_clothes = woman_category;
        } else if (!man_category.isBlank()) {
            unisex_clothes = man_category;
        }
        return unisex_clothes;
    }


    public String makeSalesInfoKey(String brandName, String season, String category, String sex) {

        StringBuilder sb = new StringBuilder();
        sb.append(brandName);
        sb.append("_");
        sb.append(season);
        sb.append("_");
        sb.append(category);
        sb.append("_");
        sb.append(sex);
        return sb.toString();
    }

    private JulianSaleInfo getJulianSaleInfo(String season, String categorySalesPercent, String brandName, String category) {
        int salesPercent = 0;

        if (!categorySalesPercent.isBlank()) {
            try {
                salesPercent = Integer.parseInt(categorySalesPercent);
            } catch (Exception e) {
            }
        }



        return JulianSaleInfo.builder()
                .brandName(brandName)
                .season(season)
                .salesPercent(salesPercent)
                .category(category)
                .build();
    }

    private String changeSeasonToWebSignature(String season) {
        switch (season) {
            case "FW25-26":
                return FALL_WINTER_2025_2026;
            case "FW24-25":
                return FALL_WINTER_2024_2025;
            case "SS24":
                return SPRING_SUMMER_2024;
            case "OUTLET":
                return OUTLET;
            case "SS25":
                return SPRING_SUMMER_2025;
            case "SALE":
                return SALE;
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
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                return cell.getCellFormula().trim();
            default:
                return ""; // 다른 타입의 셀은 빈 문자열 반환
        }
    }
}
