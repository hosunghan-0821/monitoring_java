package com.example.monitor.infra.discord;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
public class ExchangeTool {

    private double feeOption1 = 5500;
    private double feeOption2 = 3.85;


    public String calculateFee(String originFeeString) {
        double originFee = Double.parseDouble(originFeeString);
        double calculateFee = originFee - (feeOption1 + originFee * feeOption2 * 0.01);
        return getFormattedNumberString(calculateFee);

    }

    private String getFormattedNumberString(double averagePrice) {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        String averagePriceS = format.format(averagePrice);
        averagePriceS += "원";
        return averagePriceS;
    }
}
