package com.example.monitor.unit.chrome;

import chrome.ChromeDriverTool;
import chrome.ChromeDriverToolFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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