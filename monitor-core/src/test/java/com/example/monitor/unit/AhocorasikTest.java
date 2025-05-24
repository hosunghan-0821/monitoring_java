package com.example.monitor.unit;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AhocorasikTest {

    /**
     * 규칙 = (이름, 토큰 집합)
     */
    record Rule(String name, Set<String> tokens) {
    }

    /* 1) 규칙 선언 */
    private final List<Rule> ruleSets = List.of(
            new Rule("RULE_A", Set.of("1bh038", "rv44", "f0002")),
            new Rule("RULE_B", Set.of("ASD", "21", "F0001", "g1"))
    );

    /* 2) 이름 ↔ 인덱스 테이블 */
    private final List<String> ruleNames = new ArrayList<>();
    private final Map<String, Integer> nameToIndex = new HashMap<>();

    /* 3) 토큰 ↔ 규칙인덱스( ArrayList<Integer> ) */
    private final Map<String, List<Integer>> tokenToRules = new HashMap<>();

    /* 4) Trie */
    private final Trie trie;

    /* ─── 초기화 블록 ─────────────────────────────────── */ {
        /* 2-1. 이름/인덱스 매핑 */
        for (int i = 0; i < ruleSets.size(); i++) {
            ruleNames.add(ruleSets.get(i).name());
            nameToIndex.put(ruleSets.get(i).name(), i);
        }

        /* 2-2. 토큰 역-색인 & 토큰 우주 수집 */
        Set<String> allTokens = new HashSet<>();

        for (int idx = 0; idx < ruleSets.size(); idx++) {
            for (String raw : ruleSets.get(idx).tokens()) {
                String tok = raw.toUpperCase();
                allTokens.add(tok);

                tokenToRules
                        .computeIfAbsent(tok, k -> new ArrayList<>())
                        .add(idx);                      // ArrayList<Integer>
            }
        }

        /* 3. Trie 빌드 (대문자만이므로 ignoreCase 불필요) */
        trie = Trie.builder()
                .addKeywords(allTokens)
                .build();
    }
    /* ──────────────────────────────────────────────── */

    @Test
    void testEvaluate() {

        String sample = "1BH038VUOPRV44F0002".toUpperCase();

        // 규칙 인덱스(hitIdx) → 규칙 이름(hitNames) 변환
        List<Integer> hitIdx = evaluate(sample);
        List<String> hitNames = hitIdx.stream()
                .map(ruleNames::get)
                .toList();

        System.out.println("Matched = " + hitNames);   // [RULE_A]

        String sample2 = "21gggasdzxcASD1azxc123121F0001g1".toUpperCase();

        // 규칙 인덱스(hitIdx) → 규칙 이름(hitNames) 변환
        hitIdx = evaluate(sample2);
        hitNames = hitIdx.stream()
                .map(ruleNames::get)
                .toList();
        System.out.println("Matched = " + hitNames);   // [RULE_A]

    }


    /* 문자열 1건을 평가해 ‘충족한 규칙 인덱스’ 반환 */
    private List<Integer> evaluate(String s) {

        int ruleCount = ruleSets.size();
        int[] remain = new int[ruleCount];
        BitSet touched = new BitSet(ruleCount);

        for (int i = 0; i < ruleCount; i++)
            remain[i] = ruleSets.get(i).tokens().size();

        /* ① 문자열 한 번 스캔 */
        for (Emit e : trie.parseText(s)) {
            List<Integer> list = tokenToRules.get(e.getKeyword());
            if (list == null) continue;

            for (int rid : list) {
                if (remain[rid] > 0) {
                    remain[rid]--;
                    touched.set(rid);
                }
            }
        }

        /* ② 남은 토큰이 0이면 매칭 성공 */
        List<Integer> hit = new ArrayList<>();
        for (int rid = touched.nextSetBit(0); rid >= 0; rid = touched.nextSetBit(rid + 1)) {
            if (remain[rid] == 0) hit.add(rid);
        }
        return hit;
    }


    @Test
    void NonAhocorasikTest() {
        List<String> testStrings = new ArrayList<>();
        List<String> testTokens = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String string = randomAlphabetic();
            String token = randomToken();
            testStrings.add(string);
            testTokens.add(token);
        }

        /* ===== 시간 측정 시작 ===== */
        long t0 = System.nanoTime();

        for (String testString : testStrings) {
            for (String testToken : testTokens) {
                if (testString.contains(testToken)) {
                   // System.out.printf("testString: %s  contains token: %s%n", testString, testToken);
                }
            }
        }

        long elapsedNs = System.nanoTime() - t0;
        /* ===== 시간 측정 끝 ===== */

        System.out.printf("Loop elapsed time: %.3f ms (%d ns)%n",
                elapsedNs / 1_000_000.0, elapsedNs);

    }

    @Test
    void AhocoraskiTest() {
        List<String> testStrings = new ArrayList<>();
        List<String> testTokens = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String string = randomAlphabetic();
            String token = randomToken();
            testStrings.add(string);
            testTokens.add(token);
        }

        Trie tesTrie = Trie.builder()
                .addKeywords(testTokens)
                .build();

        /* ===== 시간 측정 시작 ===== */
        long t0 = System.nanoTime();

        for (String testString : testStrings) {
            Collection<Emit> emits = tesTrie.parseText(testString);
           // emits.forEach(v -> System.out.println(v.getKeyword()));
        }


        long elapsedNs = System.nanoTime() - t0;
        /* ===== 시간 측정 끝 ===== */

        System.out.printf("Loop elapsed time: %.3f ms (%d ns)%n",
                elapsedNs / 1_000_000.0, elapsedNs);

    }


    private static final SecureRandom RND = new SecureRandom();
    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * 10~20글자 사이의 무작위 영문 문자열을 반환한다.
     */
    public static String randomAlphabetic() {
        // 10~20 사이 길이 선택 (inclusive)
        int len = 10 + RND.nextInt(11);   // nextInt(11) → 0~10
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(LETTERS[RND.nextInt(LETTERS.length)]);
        }
        return sb.toString();
    }

    public static String randomToken() {
        int len = 3 + RND.nextInt(4);   // 3‒7
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(LETTERS[RND.nextInt(LETTERS.length)]);
        }
        return sb.toString();
    }
}