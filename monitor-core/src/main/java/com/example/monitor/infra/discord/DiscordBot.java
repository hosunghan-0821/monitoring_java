package com.example.monitor.infra.discord;


import chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.biffi.BiffiFindString;
import com.example.monitor.monitoring.biffi.BiffiProduct;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString;
import com.example.monitor.monitoring.gebnegozi.GebenegoziProduct;
import com.example.monitor.monitoring.julian.JulianFindString;
import com.example.monitor.monitoring.julian.JulianProduct;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import s3.service.S3UploaderService;

import java.awt.*;
import java.io.IOException;
import java.util.List;




@Slf4j
@RequiredArgsConstructor
@Component
public class DiscordBot extends ListenerAdapter {


    private final DiscordMessageProcessor discordMessageProcessor;

    private ChromeDriverToolFactory chromeDriverToolFactory;

    private S3UploaderService s3UploaderService;
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

    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);

        User user = event.getAuthor();

        //로봇이 보낸건 무시.
        if (user.isBot() && !event.getMessage().toString().startsWith("!")) {
            return;
        }

        TextChannel textChannel = event.getChannel().asTextChannel();

        Long channelId = textChannel.getIdLong();

        String returnMessage = null;
        if (channelId.equals(DiscordString.ALL_CATEGORIES_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseServerRunningOrNull(JulianFindString.ALL_CATEGORIES, event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool(JulianFindString.ALL_CATEGORIES));
        } else if (channelId.equals(DiscordString.PROMO_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseServerRunningOrNull(JulianFindString.PROMO, event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool(JulianFindString.PROMO));
        } else if (channelId.equals(DiscordString.DOUBLE_F_DISCOUNT_CHANNEL) || channelId.equals(DiscordString.DOUBLE_F_NEW_PRODUCT_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseServerRunningOrNull(DoubleFFindString.DOUBLE_F, event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool(DoubleFFindString.DOUBLE_F));
        } else if (channelId.equals(DiscordString.BIFFI_DISCOUNT_CHANNEL) || channelId.equals(DiscordString.BIFFI_NEW_PRODUCT_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseServerRunningOrNull(BiffiFindString.BIFFI, event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool(BiffiFindString.BIFFI));
        } else if (channelId.equals(DiscordString.GEBENE_NEW_PRODUCT_CHANNEL)) {
            if (event.getMessage().getContentDisplay().contains("!upload")) {
                returnMessage = discordMessageProcessor.responseServerRunningS3ServiceOrNull(event.getMessage().getContentDisplay(), s3UploaderService);
            } else {
                returnMessage = discordMessageProcessor.responseServerRunningOrNull(GebenegoziProdcutFindString.GEBE, event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool(GebenegoziProdcutFindString.GEBE));
            }
        } else if (channelId.equals(DiscordString.EXCHANGE_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseExchangeFee(event.getMessage().getContentDisplay());
        }

        if (returnMessage != null) {
            textChannel.sendMessage(returnMessage).queue();
        }
    }

    public void sendNewProductInfo(Long channelId, BiffiProduct biffiProduct, String url) {

        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("새 상품 알림!");
        embed.setDescription(
                "상품품번 : " + biffiProduct.getSku() + "\n" +
                        "상품브랜드 : " + biffiProduct.getBrandName() + "\n\n" +
                        "가격정보 \n" + biffiProduct.getPrice());
        embed.setColor(Color.magenta); // Embed 색상 설정

        embed.addField("사이트 상품 바로가기", "[BIFFI 상세페이지 바로가기](" + biffiProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.

        embed.setImage(biffiProduct.getImgUrl()); // 웹 이미지 사용
        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(biffiProduct.getSku()).queue(); // 품번도 같이 전송

    }

    public void sendNewProductInfo(Long channelId, GebenegoziProduct gebenegoziProduct) {

        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("새 상품 알림!");
        embed.setDescription(
                "상품 카테고리 : " + gebenegoziProduct.getSeason() + "\n" +
                        "상품품번 : " + gebenegoziProduct.getSku() + "\n" +
                        "상품브랜드 : " + gebenegoziProduct.getBrandName() + "/" + gebenegoziProduct.getCategory() + "\n\n" +
                        "가격정보 \n" + gebenegoziProduct.getPrice() + "\n" +
                        "wholeSale FinalPrice : " + gebenegoziProduct.getWholeSale() + "\n" +
                        "wholeSale Percent : " + gebenegoziProduct.getWholeSalePercent() + "\n" +
                        "wholeSale Origin : " + gebenegoziProduct.getWholeSaleOrigin() + "\n" +
                        "Filled Color : " + gebenegoziProduct.isColored() + "\n\n"+
                        "원산지 " + gebenegoziProduct.getMadeBy());

        embed.setColor(Color.DARK_GRAY); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[Gebene 상세페이지 바로가기](" + gebenegoziProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.

        // 이미지 추가
        embed.setImage(gebenegoziProduct.getImageSrc()); // 웹 이미지 사용
        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(gebenegoziProduct.getSku()).queue(); // 품번도 같이 전송
    }

    public void sendNewProductInfo(Long channelId, JulianProduct julianProduct) {

        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("새 상품 알림!");
        embed.setDescription(

                        "상품품번 : " + julianProduct.getSku() + "\n" +
                        "상품브랜드 : " + julianProduct.getName() + "\n\n\n" +

                        "상품 카테고리 : " + julianProduct.getCategory() + "\n" +
                        "원산지" + julianProduct.getMadeBy() + "\n" +
                        "가격정보 \n" + julianProduct.getPrice() + "\n" +
                        "wholeSale FinalPrice : " + julianProduct.getWholeSale() + "\n" +
                        "wholeSale Percent : " + julianProduct.getWholeSalePercent() + "\n" +
                        "wholeSale Origin : " + julianProduct.getWholeSaleOrigin() + "\n" +
                        "wholeSale key : " + julianProduct.getWholeSaleKeyInfo() + "\n"

        );
        embed.setColor(Color.GREEN); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[줄리앙 상세페이지 바로가기](" + julianProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.

        // 이미지 추가
        embed.setImage(julianProduct.getImageUrl()); // 웹 이미지 사용
        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(julianProduct.getSku()).queue(); // 품번도 같이 전송
    }

    public void sendNewProductInfo(Long channelId, DoubleFProduct doubleFProduct, String url) {

        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("새 상품 알림!");
        embed.setDescription(
                "상품 이름 : " + doubleFProduct.getName() + "\n" +
                        "할인율 : " + doubleFProduct.getDiscountPercentage() + "\n" +
                        "상품브랜드 : " + doubleFProduct.getBrandName() + "\n\n" +
                        "가격정보 \n" + doubleFProduct.getPrice() + "\n\n" +
                        "원산지 \n" + doubleFProduct.getMadeBy() + "\n\n");


        embed.setColor(Color.GREEN); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[DOUBLEF 상세페이지 바로가기](" + doubleFProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.


        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(doubleFProduct.getSku() + " " + doubleFProduct.getColorCode()).queue();

    }

    public void sendDiscountChangeInfo(Long channelId, BiffiProduct biffiProduct, String url, String beforeDiscount) {

        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("할인율 바뀌었습니다!!!!");
        embed.setDescription(
                "상품 품번 : " + biffiProduct.getSku() + "\n" +
                        "이전 할인율 : " + beforeDiscount + "\n" +
                        "현재 할인율 : " + biffiProduct.getDiscountPercentage() + "\n" +
                        "상품브랜드 : " + biffiProduct.getBrandName() + "\n\n" +
                        "가격정보 \n" + biffiProduct.getPrice());

        embed.setColor(Color.magenta); // Embed 색상 설정
        // 이미지 추가
        embed.setImage(biffiProduct.getImgUrl()); // 웹 이미지 사용
        embed.addField("사이트 바로가기", "[BIFFI 바로가기](" + url + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.

        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(biffiProduct.getSku()).queue(); // 품번도 같이 전송

    }

    public void sendDiscountChangeInfo(Long channelId, DoubleFProduct doubleFProduct, String url, String beforeDiscount) {

        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("할인율 바뀌었습니다!!!!");
        embed.setDescription(
                "상품 이름 : " + doubleFProduct.getName() + "\n" +
                        "이전 할인율 : " + beforeDiscount + "\n" +
                        "현재 할인율 : " + doubleFProduct.getDiscountPercentage() + "\n" +
                        "상품브랜드 : " + doubleFProduct.getBrandName() + "\n\n" +
                        "가격정보 \n" + doubleFProduct.getPrice() + "\n\n" +
                        "원산지 \n" + doubleFProduct.getMadeBy());

        embed.setColor(Color.BLUE); // Embed 색상 설정

        embed.addField("사이트 바로가기", "[DOUBLEF 바로가기](" + doubleFProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.

        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(doubleFProduct.getSku() + " " + doubleFProduct.getColorCode()).queue();
    }

    public void setS3UploaderService(S3UploaderService s3UploaderService) {
        this.s3UploaderService = s3UploaderService;
    }

    public void setChromeDriverTool(ChromeDriverToolFactory chromeDriverToolFactory) {
        this.chromeDriverToolFactory = chromeDriverToolFactory;
    }

}
