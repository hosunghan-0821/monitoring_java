package com.example.monitor.infra.discord;


import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.infra.s3.S3UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordMessageProcessor {

    public String responseServerRunningOrNull(String monitoring, String message, ChromeDriverTool chromeDriverTool){
        message = message.substring(1);
        String returnMessage = null;
        if (message.equals("on")) {
            chromeDriverTool.isRunning(true);
            returnMessage = monitoring + " turn on Monitoring" ;
        } else if (message.equals("status")) {
            returnMessage = monitoring + "is now Running " + chromeDriverTool.isRunning();
        } else if (message.equals("off")) {
            chromeDriverTool.isRunning(false);
            returnMessage = monitoring + " turn off Monitoring ";
        }
        return returnMessage;
    }

    public String responseServerRunningS3ServiceOrNull(String message, S3UploaderService s3UploaderService) {

        message = message.substring(1);
        String returnMessage = null;
        if (message.equals("upload on")) {
            s3UploaderService.setAllowedUpload(true);
            returnMessage =  "S3 Upload Service turn on";
        } else if (message.equals("upload status")) {
            returnMessage =  "S3 Upload Service status" + s3UploaderService.isAllowedUpload();
        } else if (message.equals("upload off")) {
            s3UploaderService.setAllowedUpload(false);
            returnMessage =  "S3 Upload Service turn false";
        }
        return returnMessage;
    }
}
