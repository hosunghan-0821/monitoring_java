package com.example.monitor.infra.discord;


import chrome.ChromeDriverToolFactory;
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
            returnMessage = discordMessageProcessor.responseServerRunningOrNull("all_categories", event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool("all_categories"));
        } else if (channelId.equals(DiscordString.DOUBLE_F_DISCOUNT_CHANNEL) || channelId.equals(DiscordString.DOUBLE_F_NEW_PRODUCT_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseServerRunningOrNull("doublef", event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool("doublef"));
        } else if (channelId.equals(DiscordString.BIFFI_DISCOUNT_CHANNEL) || channelId.equals(DiscordString.BIFFI_NEW_PRODUCT_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseServerRunningOrNull("biffi", event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool("biffi"));
        } else if (channelId.equals(DiscordString.STYLE_NEW_PRODUCT_CHANNEL) || channelId.equals(DiscordString.STYLE_DISCOUNT_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseServerRunningOrNull("style", event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool("style"));
        } else if (channelId.equals(DiscordString.GEBENE_NEW_PRODUCT_CHANNEL)) {
            if (event.getMessage().getContentDisplay().contains("!upload")) {
                returnMessage = discordMessageProcessor.responseServerRunningS3ServiceOrNull(event.getMessage().getContentDisplay(), s3UploaderService);
            } else {
                returnMessage = discordMessageProcessor.responseServerRunningOrNull("gebe", event.getMessage().getContentDisplay(), chromeDriverToolFactory.getChromeDriverTool("gebe"));
            }
        } else if (channelId.equals(DiscordString.EXCHANGE_CHANNEL)) {
            returnMessage = discordMessageProcessor.responseExchangeFee(event.getMessage().getContentDisplay());
        }

        if (returnMessage != null) {
            textChannel.sendMessage(returnMessage).queue();
        }
    }

    public void sendNewProductInfoCommon(Long channelId, String description, String productLink, String imageUrlOrNull, String[] skuInfo) {
        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("새 상품 알림!");
        embed.setDescription(description);
        embed.setColor(Color.GREEN);

        embed.addField("사이트 상품 바로가기", "[상세페이지 바로가기](" + productLink + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
        if (imageUrlOrNull != null) {
            embed.setImage(imageUrlOrNull); // 웹 이미지 사용
        }
        String skuInfoToString = String.join(" ", skuInfo);
        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(skuInfoToString).queue(); // 품번도 같이 전송
    }

    public void sendDiscountChangeInfoCommon(Long channelId, String description, String productLink, String imageUrlOrNull, String[] skuInfo) {
        final TextChannel textChannel = jda.getTextChannelById(channelId);
        assert (textChannel != null);

        // Embed 생성
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("할인율 바뀌었습니다!!!!");
        embed.setDescription(description);
        embed.setColor(Color.BLUE);

        embed.addField("사이트 상품 바로가기", "[상세페이지 바로가기](" + productLink + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
        if (imageUrlOrNull != null) {
            embed.setImage(imageUrlOrNull); // 웹 이미지 사용
        }
        String skuInfoToString = String.join(" ", skuInfo);
        textChannel.sendMessageEmbeds(embed.build()).queue();
        textChannel.sendMessage(skuInfoToString).queue(); // 품번도 같이 전송
    }


//    public void sendNewProductInfo(Long channelId, BiffiProduct biffiProduct) {
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("새 상품 알림!");
//        embed.setDescription(
//                "상품품번 : " + biffiProduct.getSku() + "\n" +
//                        "상품브랜드 : " + biffiProduct.getBrandName() + "\n\n" +
//                        "가격정보 \n" + biffiProduct.getPrice());
//        embed.setColor(Color.magenta); // Embed 색상 설정
//
//        embed.addField("사이트 상품 바로가기", "[BIFFI 상세페이지 바로가기](" + biffiProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//
//        embed.setImage(biffiProduct.getImgUrl()); // 웹 이미지 사용
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(biffiProduct.getSku()).queue(); // 품번도 같이 전송
//
//    }

//    public void sendNewProductInfo(Long channelId, GebenegoziProduct gebenegoziProduct) {
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("새 상품 알림!");
//        embed.setDescription(
//                "상품 카테고리 : " + gebenegoziProduct.getSeason() + "\n" +
//                        "상품품번 : " + gebenegoziProduct.getSku() + "\n" +
//                        "상품브랜드 : " + gebenegoziProduct.getBrandName() + "/" + gebenegoziProduct.getCategory() + "\n\n" +
//                        "가격정보 \n" + gebenegoziProduct.getPrice() + "\n" +
//                        "wholeSale FinalPrice : " + gebenegoziProduct.getFinalPrice() + "\n" +
//                        "wholeSale Percent : " + gebenegoziProduct.getSalePercent() + "\n" +
//                        "wholeSale Origin : " + gebenegoziProduct.getOriginPrice() + "\n" +
//                        "Filled Color : " + gebenegoziProduct.isColored() + "\n\n" +
//                        "원산지 " + gebenegoziProduct.getMadeBy());
//
//        embed.setColor(Color.DARK_GRAY); // Embed 색상 설정
//
//        embed.addField("사이트 바로가기", "[Gebene 상세페이지 바로가기](" + gebenegoziProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//
//        // 이미지 추가
//        embed.setImage(gebenegoziProduct.getImageSrc()); // 웹 이미지 사용
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(gebenegoziProduct.getSku()).queue(); // 품번도 같이 전송
//    }

//    public void sendNewProductInfo(Long channelId, JulianProduct julianProduct) {
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("새 상품 알림!");
//        embed.setDescription(
//
//                "상품품번 : " + julianProduct.getSku() + "\n" +
//                        "상품브랜드 : " + julianProduct.getName() + "\n" +
//
//                        "상품 카테고리 : " + julianProduct.getCategory() + "\n\n" +
//                        "원산지" + julianProduct.getMadeBy() + "\n\n" +
//                        "wholeSale FinalPrice : " + julianProduct.getFinalPrice() + "\n" +
//                        "wholeSale Percent : " + julianProduct.getSalePercent() + "\n" +
//                        "wholeSale Origin : " + julianProduct.getOriginPrice() + "\n\n\n" +
//                        "wholeSale key : " + julianProduct.getExcelKeyInfo() + "\n"
//
//        );
//        embed.setColor(Color.GREEN); // Embed 색상 설정
//
//        embed.addField("사이트 바로가기", "[줄리앙 상세페이지 바로가기](" + julianProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//
//        // 이미지 추가
//        embed.setImage(julianProduct.getImageUrl()); // 웹 이미지 사용
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(julianProduct.getSku()).queue(); // 품번도 같이 전송
//    }

//    public void sendNewProductInfo(Long channelId, DoubleFProduct doubleFProduct, String url) {
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("새 상품 알림!");
//        embed.setDescription(
//                "상품 이름 : " + doubleFProduct.getName() + "\n" +
//                        "할인율 : " + doubleFProduct.getDiscountPercentage() + "\n" +
//                        "상품브랜드 : " + doubleFProduct.getBrandName() + "\n\n" +
//                        "가격정보 \n" + doubleFProduct.getPrice() + "\n\n" +
//                        "원산지 \n" + doubleFProduct.getMadeBy() + "\n\n");
//
//
//        embed.setColor(Color.GREEN); // Embed 색상 설정
//
//        embed.addField("사이트 바로가기", "[DOUBLEF 상세페이지 바로가기](" + doubleFProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//
//
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(doubleFProduct.getSku() + " " + doubleFProduct.getColorCode()).queue();
//
//    }

//    public void sendDiscountChangeInfo(Long channelId, BiffiProduct biffiProduct, String url, String beforeDiscount) {
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("할인율 바뀌었습니다!!!!");
//        embed.setDescription(
//                "상품 품번 : " + biffiProduct.getSku() + "\n" +
//                        "이전 할인율 : " + beforeDiscount + "\n" +
//                        "현재 할인율 : " + biffiProduct.getDiscountPercentage() + "\n" +
//                        "상품브랜드 : " + biffiProduct.getBrandName() + "\n\n" +
//                        "가격정보 \n" + biffiProduct.getPrice());
//
//        embed.setColor(Color.magenta); // Embed 색상 설정
//        // 이미지 추가
//        embed.setImage(biffiProduct.getImgUrl()); // 웹 이미지 사용
//        embed.addField("사이트 바로가기", "[BIFFI 바로가기](" + url + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(biffiProduct.getSku()).queue(); // 품번도 같이 전송
//
//    }

//    public void sendDiscountChangeInfo(Long channelId, DoubleFProduct doubleFProduct, String url, String beforeDiscount) {
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("할인율 바뀌었습니다!!!!");
//        embed.setDescription(
//                "상품 이름 : " + doubleFProduct.getName() + "\n" +
//                        "이전 할인율 : " + beforeDiscount + "\n" +
//                        "현재 할인율 : " + doubleFProduct.getDiscountPercentage() + "\n" +
//                        "상품브랜드 : " + doubleFProduct.getBrandName() + "\n\n" +
//                        "가격정보 \n" + doubleFProduct.getPrice() + "\n\n" +
//                        "원산지 \n" + doubleFProduct.getMadeBy());
//
//        embed.setColor(Color.BLUE); // Embed 색상 설정
//
//        embed.addField("사이트 바로가기", "[DOUBLEF 바로가기](" + doubleFProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(doubleFProduct.getSku() + " " + doubleFProduct.getColorCode()).queue();
//    }

//    public void sendNewProductInfo(Long channelId, StyleProduct styleProduct) {
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("새 상품 알림!");
//        embed.setDescription(
//                "상품 이름 : " + styleProduct.getName() + "\n" +
//                        "할인율 : " + styleProduct.getSalePercent() + "\n" +
//                        "상품브랜드 : " + styleProduct.getBrandName() + "\n\n" +
//
//                        "가격정보 \n" +
//                        "wholeSale FinalPrice : " + styleProduct.getDoublePrice() + "\n" +
//                        "wholeSale Origin : " + styleProduct.getPrice() + "\n\n\n" +
//
//                        "원산지 \n" + styleProduct.getMadeBy() + "\n\n");
//
//
//        embed.setColor(Color.GREEN); // Embed 색상 설정
//        // 이미지 추가
//        embed.setImage(styleProduct.getImageUrl()); // 웹 이미지 사용
//        embed.addField("사이트 바로가기", "[STYLE 상세페이지 바로가기](" + styleProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(styleProduct.getSku()).queue();
//
//    }

//    public void sendDiscountChangeInfo(Long channelId, StyleProduct styleProduct, String discountPercentage) {
//
//
//        final TextChannel textChannel = jda.getTextChannelById(channelId);
//        assert (textChannel != null);
//
//        // Embed 생성
//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("할인율 바뀌었습니다!!!!");
//        embed.setDescription(
//                "상품 이름 : " + styleProduct.getName() + "\n" +
//                        "이전 할인율 : " + discountPercentage + "\n" +
//                        "현재 할인율 : " + styleProduct.getSalePercent() + "\n" +
//                        "상품브랜드 : " + styleProduct.getBrandName() + "\n\n" +
//                        "가격정보 \n" + styleProduct.getPrice() + "\n\n" +
//                        "원산지 \n" + styleProduct.getMadeBy());
//
//        embed.setColor(Color.BLUE); // Embed 색상 설정
//
//        embed.addField("사이트 바로가기", "[STYLE 바로가기](" + styleProduct.getProductLink() + ")", false); // false는 필드가 인라인으로 표시되지 않도록 설정합니다.
//        // 이미지 추가
//        embed.setImage(styleProduct.getImageUrl()); // 웹 이미지 사용
//        textChannel.sendMessageEmbeds(embed.build()).queue();
//        textChannel.sendMessage(styleProduct.getSku()).queue();
//
//    }

    public void setS3UploaderService(S3UploaderService s3UploaderService) {
        this.s3UploaderService = s3UploaderService;
    }

    public void setChromeDriverTool(ChromeDriverToolFactory chromeDriverToolFactory) {
        this.chromeDriverToolFactory = chromeDriverToolFactory;
    }


}
