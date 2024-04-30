package com.example.monitor.integration;

import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.any;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {

    @Bean
    @Primary
    DiscordBot getDiscordBot() {

        DiscordBot discordBot = Mockito.mock(DiscordBot.class);
        Mockito.doNothing().when(discordBot).sendNewProductInfo(any(), any(DoubleFProduct.class),any());
        return discordBot;

    }
}
