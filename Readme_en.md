# Monitoring & Auto-Order Application  

> Personal project for **Monitoring Overseas Boutique Product Prices/Discounts → Profitability Check → Automated Ordering**  

---

## 📦 Repository Structure  

| Role | Repository | Tech Stack | Notes |
| --- | --- | --- | --- |
| Monitoring App (current repo) | **monitoring_java** | Java, Spring Boot, Selenium | Price/discount monitoring, triggers auto-order |
| Search & Order Server | [search_java](https://github.com/hosunghan-0821/search_java) | Java, Spring Boot | Product code search, order API |
| Admin Page | [Monitoring_Admin](https://github.com/hosunghan-0821/Monitoring_Admin) | React, Spring | Product code/config management, report view |

---

## 📅 Duration & Team  
- **Personal Project**  
- **2024.04.21 ~ Ongoing**  

---

## 🛠 Tech Stack  
- **Backend**: Java, Spring Boot  
- **Frontend**: React  
- **Crawling**: Selenium, ChromeDriver  
- **Infra / Others**: Discord Bot API, Scouter APM, VPN/Proxy  

---

## 🎯 Service Description  

- Periodically **monitors product prices and discounts** on multiple overseas boutique websites  
- Sends **Discord alerts** when price/discount changes are detected  
- Cross-checks products on KREAM (domestic resale platform) and sends an additional alert if profit margin exceeds a configured threshold  
- When a specific product code is uploaded and managed in the Admin panel, if the price/quantity matches configured conditions, the app **automatically places an order on the boutique site**  

### 🖼 Monitoring & Comparison Report Examples  
![monitoring-report-1](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/0290427f-29a0-489b-bf58-bed27fff2a1c)  
![monitoring-report-2](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/a448bf6d-463b-4627-b29a-00d01edebb74)  

---

## 🏗 Overall Architecture  

![architecture](https://github.com/user-attachments/assets/49d3e37d-663b-4049-9336-b561b0b798bb)  

---

## 🔍 Monitor-Core Application Structure  

![monitor-core-structure](https://github.com/hosunghan-0821/monitoring_java/assets/79980357/6b371a3b-0c0e-4149-a24f-5daab6e02de2)  

---

## 🧠 Technical Challenges & Solutions  

### 0. Stability of UI-Dependent Crawling Logic  
- Stored external site HTML snapshots for **regression testing**  
- Split into **unit tests** (service layer) and **integration tests** (actual site interactions)  

### 1. Monitoring Multiple Sites Simultaneously  
- Used Spring `@Scheduled` + Java Thread Pool for **parallel execution**  

### 2. Normalizing Product Code Formats  
- Different product code formats per site unified via a **Converting Layer**  
- Mapping is loaded from configuration files at startup  

### 3. Anti-Bot Evasion  
- Applied **think time** between requests  
- **Rotated User-Agent / Cookies**  
- Configured ChromeDriver to mimic normal user behavior  
- Used **VPN/Proxy**  

### 4. Testing Strategy Separation  
- **Core logic**: covered with unit tests by removing external dependencies  
- **Integration parts**: validated with real-world interactions via integration tests  

### 5. Resource/Performance Optimization  
- Minimized **DB access**, prioritized in-memory comparison logic  
- Stored only necessary data in **file-based storage (DB alternative)**  
- Used **Scouter APM** to monitor Heap / Young GC  

---

## ✅ Quality Assurance & Reliability  

- **Snapshot-based regression tests** to detect UI changes  
- **Smoke tests** to verify pipeline health  
- Leveraged Discord alert channels for **operational monitoring**  
