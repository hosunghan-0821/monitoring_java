package s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploaderService {

    private final AmazonS3Client amazonS3Client;

    @Setter
    @Getter
    private boolean isAllowedUpload = true;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImageOrNull(File file, String fileName) {

        assert (file != null);
        assert (fileName != null);

        if (!isAllowedUpload) {
            file.delete();
            return null;
        }
        //상품이 있으면 재업로드 안해도됨
        boolean isUpload = amazonS3Client.doesObjectExist(bucket, fileName);

        String accessUrl = null;
        if (!isUpload) {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, file)
                            .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
            );
            accessUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        } else {
            accessUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        }
        file.delete();


        return accessUrl;
    }

}
