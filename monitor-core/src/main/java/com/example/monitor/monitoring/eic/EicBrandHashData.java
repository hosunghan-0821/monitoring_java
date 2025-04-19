package com.example.monitor.monitoring.eic;

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
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EicBrandHashData {

    private final HashMap<String, HashMap<String, EicProduct>> eicProductHashMap;

    /*
     * Key: 파일 상세 링크
     * Value: 상품 정보
     * */
    @Getter
    private final HashMap<String, EicProduct> eicDiscountProductHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public EicBrandHashData() {
        eicProductHashMap = new HashMap<>();
        eicDiscountProductHashMap = new HashMap<>();

        for (String brandName : EicFindString.brandNameList) {
            eicProductHashMap.put(brandName, new HashMap<>());
        }

        productKeySet = new HashSet<>();
    }


    public Map<String, EicProduct> getBrandHashMap(String brandName) {
        return eicProductHashMap.get(brandName);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initEicData() {
        List<File> xlsxFiles = findXlsxFiles();
        for (File file : xlsxFiles) {
            String fileName = file.getName();
            log.info(fileName + " sales info 데이터 로드 시작");

            try (
                    Workbook workbook = WorkbookFactory.create(file);
            ) {

                Sheet sheet = workbook.getSheetAt(0);
                int startIndex = 1;
                int lastIndex = sheet.getLastRowNum();
                for (int i = startIndex; i <= lastIndex; i++) {
                    Row row = sheet.getRow(i);

                    //brand이름이 비어있으면 패스
                    String productDetailLink = getCellValue(row.getCell(1)); // 1,1부터 데이터 링크 들어있음.
                    if (productDetailLink.isEmpty() || productDetailLink.isBlank()) {
                        continue;
                    }
                    eicDiscountProductHashMap.putIfAbsent(productDetailLink, EicProduct.builder().productLink(productDetailLink).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("액셀 데이터 로드 오류");
            }

            log.info(fileName + " sales info 데이터 로드 완료");
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
                    if (fileName.endsWith(".xlsx") && fileName.contains("GILIO")) {
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
