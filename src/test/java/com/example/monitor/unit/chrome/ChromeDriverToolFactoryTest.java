package com.example.monitor.unit.chrome;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChromeDriverToolFactoryTest {

    @InjectMocks
    private ChromeDriverToolFactory chromeDriverToolFactory;

    @Test
    @DisplayName("[Chrome] : chromeDriverTool Test")
    void makeChromeDriverTool() {
        //given
        chromeDriverToolFactory.makeChromeDriverTool("test");

        //when
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool("test");
        //then
        Assertions.assertThat(chromeDriverTool.getChromeDriver()).isNotNull();
        Assertions.assertThat(chromeDriverTool.getWebDriverWait()).isNotNull();

        chromeDriverTool.getChromeDriver().quit();
    }
}