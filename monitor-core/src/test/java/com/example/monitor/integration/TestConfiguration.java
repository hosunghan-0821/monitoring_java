package com.example.monitor.integration;

import module.discord.DiscordBot;
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
        Mockito.doNothing().when(discordBot).sendNewProductInfoCommon(any(),any(),any(),any(),any());
        return discordBot;

    }
}
