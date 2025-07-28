# Monitoring & Auto-Order Application

> 해외 부띠끄 상품 **가격/할인율 모니터링 → 수익성 검증 → 자동 주문**까지 처리하는 개인 프로젝트

---

## 📦 Repository 구성

| 역할 | 저장소 | 주요 기술 | 비고 |
| --- | --- | --- | --- |
| 모니터링 앱 (현재 Repo) | **monitoring_java** | Java, Spring Boot, Selenium | 가격/할인율 모니터링, 자동 주문 트리거 |
| 검색 · 주문 서버 | [search_java](https://github.com/hosunghan-0821/search_java) | Java, Spring Boot | 품번 검색, 주문 API |
| 관리자 페이지 | [Monitoring_Admin](https://github.com/hosunghan-0821/Monitoring_Admin) | React, Spring | 품번/설정 관리, 리포트 조회 |

---

## 📅 기간 & 인원
- **개인 프로젝트**
- **2024.04.21 ~ 진행 중**

---

## 🛠 기술 스택
- **Backend**: Java, Spring Boot
- **FronetEnd**: React
- **Crawling**: Selenium, ChromeDriver
- **Infra / 기타**: Discord Bot API, Scouter APM, VPN/Proxy

---

## 🎯 서비스 설명

- 여러 해외 부띠끄 사이트의 **상품 가격 및 할인율을 주기적으로 모니터링**  
- 가격/할인율 변동 발생 시 **Discord 봇으로 알림 전송**  
- 변동된 상품을 크림(국내 리셀 플랫폼)에서 검색해 **설정한 마진율 이상이면 추가 알림**  
- **특정 품번 업로드 시**, Admin에서 관리 중인 품번이고 특정 가격 및 수량에 해당하면 **해당 부띠끄에 자동 주문**

### 🖼 모니터링 결과/비교 리포트 예시
![monitoring-report-1](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/0290427f-29a0-489b-bf58-bed27fff2a1c)
![monitoring-report-2](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/a448bf6d-463b-4627-b29a-00d01edebb74)

---

## 🏗 전체 아키텍처

![architecture](https://github.com/user-attachments/assets/49d3e37d-663b-4049-9336-b561b0b798bb)

---

## 🔍 Monitor-Core Application 구조

![monitor-core-structure](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/6b371a3b-0c0e-4149-a24f-5daab6e02de2)

---

## 🧠 기술적 고민 & 구현

### 0. UI 의존성 높은 크롤링 로직의 안정성
- 외부 사이트 HTML 스냅샷을 저장해 **회귀 테스트** 수행  
- Service 레이어는 **Unit Test**, 실제 사이트 연동은 **통합 테스트**로 분리

### 1. 다수 사이트 동시 모니터링
- Spring `@Scheduled` + Java Thread Pool로 **병렬 실행**  

### 2. 사이트별 품번 포맷 변환
- 사이트마다 다른 품번 포맷을 **Converting Layer**로 통합 처리  
- 설정파일을 기동 시 로드해 런타임에서 매핑

### 3. 봇 차단 대응
- 요청 간 **Think Time** 적용  
- **User-Agent / Cookie 로테이션**  
- ChromeDriver 환경을 일반 사용자처럼 세팅  
- VPN/Proxy 활용

### 4. 테스트 전략 분리
- **핵심 로직**: 외부 의존 제거 후 단위 테스트로 커버  
- **외부 연동부**: 실제 상호작용은 통합 테스트로 검증

### 5. 리소스/성능 최적화
- **DB 접근 최소화**, In-Memory 비교 로직 우선  
- 필요한 데이터만 **파일 기반 저장(DB 대체)**  
- Scouter APM으로 **Heap / Young GC 모니터링**

---

## ✅ 품질 보증 & 안정성
- **Snapshot 기반 회귀 테스트**로 UI 변경 감지  
- **Smoke Test**로 파이프라인 정상 동작 확인  
- Discord 알림 채널을 **운영 모니터링**에도 활용
