package com.example.monitor.file;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProductFileWriterImpl implements ProductFileWriter {


    public static final String DIRECTORY_ROUTE_PATH = "./product";

    @Override
    public void fileFolderInit() throws IOException {
        File directory = new File(DIRECTORY_ROUTE_PATH);
        // 폴더 존재 여부와 폴더인지 확인
        if (!(directory.exists() && directory.isDirectory())) {
            boolean newDirectory = directory.mkdir();
            if (newDirectory) {
                log.info("new Directory make");
            }
        }
    }

    @Async
    @Override
    public void writeProductInfo(ProductFileInfo productFileInfo)  {

        try{
            this.fileFolderInit();

            String formattedDate = getTodayDate();
            String filePath = DIRECTORY_ROUTE_PATH + "/" + formattedDate + ".txt";

            this.writeHeader(filePath);
            FileWriter fw = new FileWriter(filePath, true);
            String productFileData = String.format(
                    "%10s|%15s|%20s|%12s|%12s|%20s|%20s|%20s\n",
                    productFileInfo.getMonitoringSite(),
                    productFileInfo.getBrandName(),
                    productFileInfo.getSku(),
                    productFileInfo.getColorCode(),
                    productFileInfo.getMadeBy(),
                    productFileInfo.getPrice(),
                    productFileInfo.getDetectedCause(),
                    productFileInfo.getDetectedDate()

            );

            fw.write(productFileData);

            fw.close();
        } catch (Exception e){
            e.printStackTrace();
            log.error("상품 정보 입력 오류");
        }

    }

    private void writeHeader(String path) {
        try {
            if (!Files.exists(Path.of(path))) {
                FileWriter fw = new FileWriter(path);
                String header = String.format(
                        "%10s|%15s|%20s|%12s|%12s|%20s|%20s|%20s\n\n",
                        "site",
                        "brand",
                        "sku",
                        "color code",
                        "made by",
                        "price",
                        "detected cause",
                        "detected date"
                );
                fw.write(header);
                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("file write error");
        }


    }

    private String getTodayDate() {
        // 현재 날짜 얻기
        LocalDate today = LocalDate.now();

        // 포매터 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 날짜를 지정된 포맷으로 변환
        return today.format(formatter);

    }


}
