package com.example.monitor.Util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private RandomUtil(){
        throw new RuntimeException("cannot construct util class");
    }

    /**
     * 주어진 시작 시간과 끝 시간 사이의 랜덤한 초 값을 반환합니다.
     *
     * @param startSeconds 시작 초 (포함)
     * @param endSeconds 끝 초 (포함)
     * @return 시작 초와 끝 초 사이의 랜덤 값
     * @throws IllegalArgumentException 시작 초가 끝 초보다 클 경우 예외 발생
     */
    public static int getRandomSec(int startSeconds, int endSeconds){

        if (startSeconds > endSeconds) {
            throw new IllegalArgumentException("시작 초는 끝 초보다 작아야 합니다.");
        }
        return ThreadLocalRandom.current().nextInt(startSeconds, endSeconds + 1);

    }
}
