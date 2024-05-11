package com.example.monitor.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploaderService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(File file, String fileName) {

        assert (file != null);
        assert (fileName != null);
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, file)
                        .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
        );
        String accessUrl = amazonS3Client.getUrl(bucket, fileName).toString();

        boolean delete = file.delete();

        if (delete) {
            log.info("upload 후 로컬 파일삭제");
        }

        return accessUrl;
    }

}
