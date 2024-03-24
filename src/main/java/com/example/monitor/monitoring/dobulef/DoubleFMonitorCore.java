package com.example.monitor.monitoring.dobulef;


import com.example.monitor.discord.DiscordBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoubleFMonitorCore {

    private final DiscordBot discordBot;

    @Value("${doublef.user.id}")
    private String userId;

    @Value("${doublef.user.pw}")
    private String userPw;


    public void acceptCookie(ChromeDriver driver){

    }

    public void login(ChromeDriver driver){

    }
}
