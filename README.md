# Monitoring Application

# Intro
- 위 어플리케이션은 2개의 프로젝트로 구성되었습니다. 
  - 모니터링 Application - 현재 레포 
  - 검색 Application - [github link](https://github.com/hosunghan-0821/search_java)


# 📅 기간 및 인원

- 개인프로젝트
- 2024/04/21 ~ 2024/05/05 (약 3주)

# 📚 기술스택
- [Java / SpringBoot / Selenium / Discord Bot API]  

# 🎬 서비스 설명

- 해외의 여러 부띠끄 사이트의 상품 가격 및 할인율 모니터링하는 프로그램입니다. 
- 가격 및 할인율이 변경될 경우, 디스코드 봇을 통해 알림을 보냅니다.
- 가격 및 할인율이 변동되었을 떄, 크림(국내 리셀 플랫폼)에 품번을 검색해 설정한 마진율이 높을 경우 디스코드 알람을 보냅니다.
- 하위 그림들은 모니터링 결과와 비교리포트 입니다.
<img width="934" alt="image" src="https://github.com/hosunghan-0821/monitoring_java/assets/79980357/0290427f-29a0-489b-bf58-bed27fff2a1c">
<img width="949" alt="image" src="https://github.com/hosunghan-0821/monitoring_java/assets/79980357/a448bf6d-463b-4627-b29a-00d01edebb74">

# Application 구조 
<img width="1355" alt="image" src="https://github.com/hosunghan-0821/monitoring_java/assets/79980357/6b371a3b-0c0e-4149-a24f-5daab6e02de2">


# 기술적 고민 및 구현

1. 여러 사이트를 주기적으로 동시에 모니터링 해야하는 상황을 Spring에서 지원하는 스케줄러와 자바에서 지원하는 Thread를 통해 멀티스레딩으로 풀어냈습니다. <br><br>
2. 각 패키지간의 의존성을 고려하여 설계하였습니다. <br>(위의 이미지의 화살표는 패키지간의 의존성을 나타냅니다.) <br>(추후에 멀티모듈 프로젝트로 변경하여, 모듈간 의존성 설정을 통해 모듈의 재사용성을 높이고 코드 격리를 시킬 생각입니다.)<br><br>
3. 각 모니터링 사이트마다, 상품의 품번을 다양한 방식으로 제공해서, 검색 API로 보낼 때 각 사이트마다 알맞게 Converting 할 수 있도록 설정파일을 설정하고 프로그램 시작시 로드하여 동작하도록 하였습니다.   <br><br>
4. 해외 사이트의 봇 차단을 할 때를 고려하여, 여러 방법을 통해 문제를 대비하였습니다.
   - 데이터 스크래핑할 때 필요한 사이트 요청간의 Think Time을 주었습니다. 
   - HTTP Client의 User-Agent, Cookie를 다양하게 설정하여 시도하였습니다.
   - ChromeClient의 Settings을 일반 유저처럼 세팅하였습니다.
   - 유료 솔루션인 VPN을 활용하여 IP를 우회하여, 탐색을 시도하였습니다. 
   - 유료 솔루션인 Bright Data를 통해 Proxy Server 와 Remote Chrome Driver를 활용하여 데이터 스크래핑을 시도하였습니다.<br><br>
5. 외부 사이트에 의존을 많이 하는 어플리케이션이여서, 의존하는 부분을 최소화하여 로직적으로 중요한 유닛테스트를 실시하였습니다. <br>외부 사이트에 의존하는 부분은 통합테스트로 동작을 검증하였습니다.<br><br>
6. 주기적으로 모니터링 하는 어플리케이션이다 보니, 외부 DB 작업은 불필요하다고 판단하였고, In-Memory를 통해 로직을 수행하였습니다.
   - 모니터링할 사이트가 많아짐에 따라, 어플리케이션의 안전성을 확인하기 위해 Scouter APM을 통해 heap-memory 사용량과 JVM Young GC작동을 확인하였습니다.
   - 중간에 최소한의 DB 작업이 필요한 내용들이 있었습니다. 이 때 File을 DB로써 활용하여 처리하였습니다.
