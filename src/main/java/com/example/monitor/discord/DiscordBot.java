package com.example.monitor.discord;

import com.example.monitor.monitoring.Product;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DiscordBot extends ListenerAdapter {

    private Map<String, String> channelHashMap = new HashMap<>();
    private JDA jda;
    @Value("${discord.bot.token}")
    private String discordBotToken;

    @PostConstruct
    public void init() throws IOException {
        jda = JDABuilder.createDefault(discordBotToken)
                .setActivity(Activity.playing("서버 실행중"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<TextChannel> textChannels = jda.getTextChannels();
        //In-Memory로 들고 있는게 낫지 않은가?
        for (TextChannel textChannel : textChannels) {
            log.info("ID :" + textChannel.getId() + "NAME: " + textChannel.getName());
            channelHashMap.put(textChannel.getName(), textChannel.getId());
        }
    }

    public void sendMessage(String channelName, String message) {

        final String id = channelHashMap.get(channelName);
        final TextChannel textChannel = jda.getTextChannelById(id);
        if (textChannel != null) {
            textChannel.sendMessage(message).queue();
        } else {
            log.error("유효하지 않은 채널이름 : {}", channelName);
        }
    }

    public void sendNewProductInfo(String channelName, Product product) {
        final String id = channelHashMap.get(channelName);
        final TextChannel textChannel = jda.getTextChannelById(id);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("새 상품 알림!");
        embed.setDescription(
                "상품 카테고리 : " + product.getCategory() + "\n" +
                        "상품품번 : " + product.getId() + "\n" +
                        "상품브랜드 : " + product.getName() + "\n\n" +
                        "가격정보 \n" + product.getPrice());
        embed.setColor(Color.GREEN); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[줄리앙 바로가기](https://b2bfashion.online/)", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.

        // 이미지 추가
        embed.setImage(product.getImageSrc()); // 웹 이미지 사용
        textChannel.sendMessageEmbeds(embed.build()).queue();
    }


    private String makeReturnMessage(MessageReceivedEvent event, String message) {

        /*
         * 주문정보 Return 하는 것 만들기
         *
         * */
        //여기서 주문정보 get 하는것 정도는 충분히 가능할거 같다.
        User user = event.getAuthor();
        String returnMessage = "";
        switch (message) {
            case "이름":
                returnMessage = user.getName();
                break;
            case "태그":
                returnMessage = user.getAsTag();
                break;
            case "테스트":
                returnMessage = user.getAsMention();
                break;
            default:
                returnMessage = "없는 명령어 입니다 :(";
                break;
        }
        return returnMessage;
    }
}
