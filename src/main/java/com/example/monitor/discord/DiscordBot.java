package com.example.monitor.discord;

import com.example.monitor.exchange.ExchangeCore;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.julian.JulianProduct;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.monitor.discord.DiscordString.EXCHANGE_CHANNEL;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_MAIN_PAGE;

@Slf4j
@RequiredArgsConstructor
@Component
public class DiscordBot extends ListenerAdapter {

    private Map<String, String> channelHashMap = new HashMap<>();
    private JDA jda;

    private final ExchangeCore exchangeCore;
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


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);

        User user = event.getAuthor();
        TextChannel textChannel = event.getChannel().asTextChannel();
        if (textChannel.getName().equals(EXCHANGE_CHANNEL)) {
            Message message = event.getMessage();

            String plainMessage = message.getContentDisplay();

            if (user.isBot()) {
                return;
            } else {
                log.info(user.getName() + " 's Message : " + message.getContentDisplay());
                if (message.getContentDisplay().startsWith("!")) {
                    plainMessage = plainMessage.replace("!", "");
                    exchangeCore.getExchangeRateInfo();

//                    String returnMessage = makeReturnMessage(event, plainMessage);
//                    textChannel.sendMessage(returnMessage).queue();
                }
            }
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

    public void sendNewProductInfo(String channelName, JulianProduct julianProduct) {
        final String id = channelHashMap.get(channelName);
        final TextChannel textChannel = jda.getTextChannelById(id);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("새 상품 알림!");
        embed.setDescription(
                "상품 카테고리 : " + julianProduct.getCategory() + "\n" +
                        "상품품번 : " + julianProduct.getId() + "\n" +
                        "상품브랜드 : " + julianProduct.getName() + "\n\n" +
                        "가격정보 \n" + julianProduct.getPrice());
        embed.setColor(Color.GREEN); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[줄리앙 바로가기](https://b2bfashion.online/)", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.

        // 이미지 추가
        embed.setImage(julianProduct.getImageSrc()); // 웹 이미지 사용
        textChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendNewProductInfo(String channelName, DoubleFProduct doubleFProduct,String url) {
        final String id = channelHashMap.get(channelName);
        final TextChannel textChannel = jda.getTextChannelById(id);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("새 상품 알림!");
        embed.setDescription(
                "상품 이름 : " + doubleFProduct.getNameId() + "\n" +
                        "할인율 : " + doubleFProduct.getDiscountPercentage() + "\n" +
                        "상품브랜드 : " + doubleFProduct.getBrand() + "\n\n" +
                        "가격정보 \n" + doubleFProduct.getPrice());
        embed.setColor(Color.GREEN); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[DOUBLEF 바로가기](" + url + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.


        textChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendDiscountChangeInfo(String channelName, DoubleFProduct doubleFProduct,String url, String beforeDiscount) {
        final String id = channelHashMap.get(channelName);
        final TextChannel textChannel = jda.getTextChannelById(id);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("할인율 바뀌었습니다!!!!");
        embed.setDescription(
                "상품 이름 : " + doubleFProduct.getNameId() + "\n" +
                        "이전 할인율 : " + beforeDiscount + "\n" +
                        "현재 할인율 : " + doubleFProduct.getDiscountPercentage() + "\n" +
                        "상품브랜드 : " + doubleFProduct.getBrand() + "\n\n" +
                        "가격정보 \n" + doubleFProduct.getPrice());

        embed.setColor(Color.BLUE); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[DOUBLEF 바로가기](" + url + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.


        textChannel.sendMessageEmbeds(embed.build()).queue();
    }


    private String makeReturnMessage(MessageReceivedEvent event, String message) {


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
