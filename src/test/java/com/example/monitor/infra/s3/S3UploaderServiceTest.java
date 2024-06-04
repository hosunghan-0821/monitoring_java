package com.example.monitor.infra.s3;

import com.example.monitor.monitoring.gebnegozi.GebenegoziMonitorCore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class S3UploaderServiceTest {




    @Autowired
    S3UploaderService s3UploaderService;

    @Test
    @Disabled
    public void testS3(){
        File file = new File("2024-05-10_20:46:57.jpg");
        s3UploaderService.uploadImageOrNull(file,"2024-05-10_20:46:57.jpg");
    }

}