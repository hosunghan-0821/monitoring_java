package com.example.monitor.infra.s3;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import s3.service.S3UploaderService;

import java.io.File;

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