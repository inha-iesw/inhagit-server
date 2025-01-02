<div align="center">
  <br />
  <img src = "https://github.com/user-attachments/assets/794d5573-97cf-44e6-9e3f-156c0021682b", width="200px" height="200px"></img>
  <br />
  <h1>인하대학교 오픈소스 공유 플랫폼(I-OSS)</h1>
  <div>
    <img src="https://img.shields.io/badge/NGINX-Active-2ea04?&logo=nginx&logoColor=white" alt="NGINX" />
    <img src="https://img.shields.io/github/v/release/inha-iesw/inhagit-server?color=%23068fc6" alt="GitHub release (latest by date)" />
  </div>
  <br />
</div>

## 목차

1. [**웹 서비스 소개**](#1)
1. [**기술 스택**](#2)
1. [**주요 기능**](#3)
1. [**프로젝트 구성도**](#4)
1. [**개발 팀 소개**](#5)
1. [**개발 기간 및 일정**](#6)
1. [**실행 방법**](#7)

<br />

<br />

<div id="1"></div>

## 💁 웹 서비스 소개


I-OSS는 인하대학교 IT 인프라팀과 협업하여 개발된 웹 기반 오픈소스 소프트웨어(SW) 공유 플랫폼입니다.

이 플랫폼은 인하대학교 학생, 교수진, 기업체 및 전문가 간의 협업을 촉진하고, 오픈소스 SW 생태계 성장을 지원하기 위해 설계되었습니다.

Local 혹은 GitHub과 연동하여 프로젝트를 관리하고, 멘토링, 특허·저작권 관리, 통계 시각화 등 다양한 기능을 제공합니다.

이를 통해 인하대학교의 오픈소스 SW의 활용 능력과 협업 역량을 강화하는 것을 목표로 합니다.


<br />

- 'I-OSS' 게스트 계정 정보

|  아이디  | test@gmail.com |
| :------: | :------------------ |
| 비밀번호 | password2@        |

> 서비스를 구경하고 싶으시다면 상단의 계정 정보로 로그인 후 사용하실 수 있습니다.

<br />

[**🔗 배포된 웹 서비스로 바로가기 Click !**](https://oss.inha.ac.kr/) 👈

[**🔗 서비스 데모 영상 바로가기 Click !**](https://youtu.be/WqZikpeeBe0) 👈

[**🔗 서버 API 문서 바로가기  Click !**](https://inha-iesw.github.io/inhagit-server-docs/) 👈

[**🔗 개발 서버 스웨거 바로가기  Click !**](http://165.246.21.232:8080/swagger-ui/index.html#/) 👈


> 새 창 열기 방법 : CTRL+click (on Windows and Linux) | CMD+click (on MacOS)

<br />

<div id="2"></div>

## 🛠 기술 스택

### **Front-end**

| <img src="https://profilinator.rishav.dev/skills-assets/html5-original-wordmark.svg" alt="HTML5" width="50px" height="50px" /> | <img src="https://profilinator.rishav.dev/skills-assets/css3-original-wordmark.svg" alt="CSS3" width="50px" height="50px" /> | <img src="https://profilinator.rishav.dev/skills-assets/javascript-original.svg" alt="JavaScript" width="50px" height="50px" /> | <img src="https://profilinator.rishav.dev/skills-assets/react-original-wordmark.svg" alt="React.js" width="50px" height="50px" /> |
| :----------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------: |
|                                                             HTML5                                                              |                                                             CSS3                                                             |                                                        JavaScript                                                        |                                                               React                                                               |

---

### **Back-end**

| <img src="https://profilinator.rishav.dev/skills-assets/java-original-wordmark.svg" alt="Java" width="50px" height="50px" /> | <img src="https://www.seekpng.com/png/full/8-80775_spring-logo-png-transparent-spring-java.png" alt="Spring Boot" width="50px" height="50px" /> | <img src="https://profilinator.rishav.dev/skills-assets/postgresql-original-wordmark.svg" alt="PostgreSQL" width="50px" height="50px" /> | <img src="https://profilinator.rishav.dev/skills-assets/redis-original-wordmark.svg" alt="Redis" width="50px" height="50px" /> |
| :--------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------: |
|                                                             Java                                                             |                                                                   Spring Boot                                                                   |                                                               PostgreSQL                                                               |                                                             Redis                                                              |

| <img src="https://res.cloudinary.com/postman/image/upload/t_team_logo/v1629869194/team/2893aede23f01bfcbd2319326bc96a6ed0524eba759745ed6d73405a3a8b67a8" alt="Postman" width="50px" height="50px" /> | <img src="https://upload.wikimedia.org/wikipedia/commons/a/ab/Swagger-logo.png" alt="Swagger" width="50px" height="50px" /> | <img src="https://profilinator.rishav.dev/skills-assets/nginx-original.svg" alt="NGiNX" width="50px" height="50px" /> |
| :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------: |
|                                                                                               Postman                                                                                                |                                                           Swagger                                                           |                                                         Nginx                                                         |



<div id="3"></div>

## 💡 주요 기능

| 카테고리                 | 기능                                                                                              |
| :----------------------- | :------------------------------------------------------------------------------------------------ |
| **프로젝트 관리**         | 로컬 및 GitHub 연동을 통한 프로젝트 업로드, 공지사항 게시, 댓글 및 좋아요 기능 제공합니다.               |
| **멘토링 및 커뮤니케이션** | 질문 게시판(멘토링) 및 버그 제보, 유저 신고 기능을 통해 사용자 간 소통 및 문제 해결 지원합니다.          |
| **관리자 기능**           | 교수/기업 가입 승인, 관리자/조교 승격, 유저 차단 등 플랫폼 관리 기능 제공합니다.                        |
| **데이터 검색 및 분석**   | 프로젝트, 질문 게시물 등의 검색 기능 제공 및 통계 시각화, 데이터 엑셀 추출 지원합니다.             |


<br />

<div id="4"></div>

## 📂 프로젝트 구성도

|                                   아키텍처(Architecture)                                   |
| :----------------------------------------------------------------------------------------: |
| <img src="./readme_assets/architecture.png" alt="아키텍처(Architecture)" width="1000px" /> |

|                              개체-관계 모델(ERD)                               |
| :----------------------------------------------------------------------------: |
| <img src="https://github.com/user-attachments/assets/cef79475-f1e4-41f2-a446-6d49c46eeb05" alt="개체-관계 모델(ERD)" width="1000px" /> |

<br />



<div id="5"></div>

## 👪 개발 팀 소개

<table>
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/dayjiwon" target="박지원">
        <img src="https://avatars.githubusercontent.com/u/87654321?v=4" alt="박지원" />
      </a>
    </td>
    <td align="center" width="150px">
      <a href="https://github.com/Gyuhyeok99" target="gyuhyeok99">
        <img src="https://github.com/user-attachments/assets/347fc96e-2b51-4996-ba2b-a1de1cc5c52e" alt="황규혁" />
      </a>
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/dayjiwon" target="박지원">
        박지원<br />(Front-end)
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/Gyuhyeok99" target="황규혁">
        황규혁<br />(Back-end)
      </a>
    </td>
  </tr>
</table>


<div id="6"></div>

## 📅 개발 기간

24.08.12. ~ 운영 관리 중

<br />

<div id='7'></div>

## 💻 실행 방법



###  Server 실행

1. **원격 저장소 복제**

```bash
$ git clone https://github.com/inha-iesw/inhagit-server
```

2. **프로젝터 폴더 > src > main > resources 이동**

```bash
$ cd src
$ cd main
$ cd resources
```

3. **프로젝트 실행을 위한 yml 파일 작성**

- 프로젝트 첫 빌드시 `jpa:hibernate:ddl-auto:create` 로 작성
- 이후에는 `jpa:hibernate:ddl-auto:none` 으로 변경
- 프로필 local로 설정

```bash

spring:
  config:
    activate:
      on-profile: local
  application:
    name: inhagit
  datasource:
    url: [DB설정]
    username: [DB사용자명]
    password: [DB비밀번호]
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: none
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    generate-ddl: false
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 1000
  servlet:
    multipart:
      enabled: true
      max-file-size: 300MB
      max-request-size: 300MB
  data:
    redis:
      host: [호스트]
      port: [포트]
  mail:
    host: [호스트]
    port: [포트]
    username: [이메일계정]
    password: [이메일패스워드]
    properties:
      mail:
        smtp:
          starttls:
            enable: true
          auth: true
jwt:
  issuer: [이슈자]
  secret_key: [시크릿키]
  expiration: [엑세스토큰 만료시간]
  refresh-token:
    expiration: [리프레시토큰 만료시간]

cloud:
  aws:
    s3:
      bucket: [버킷 이름]
    stack:
      auto: false
    region:
      static: [리전 이름]
    credentials:
      instance-profile: true
      access-key: [엑세스키]
      secret-key: [시크릿키]

logging:
  pattern:
    dateformat: yyyy-MM-dd HH:mm:ss.SSSz,Asia/Seoul

kipris:
  access-key: [엑세스키]
  inventor-url: http://plus.kipris.or.kr/openapi/rest/patUtiModInfoSearchSevice/patentInventorInfo
  applicant-url: http://plus.kipris.or.kr/openapi/rest/patUtiModInfoSearchSevice/patentApplicantInfo
  basic-info-url: http://plus.kipris.or.kr/kipo-api/kipi/patUtiModInfoSearchSevice/getBibliographySumryInfoSearch


user:
  basedir: [베이스경로]
  file: [파일경로]


management:
  endpoints:
    web:
      exposure:
        include: prometheus
  endpoint:
    prometheus:
      enabled: true
server:
  tomcat:
    mbeanregistry:
      enabled: true

```

4. **프로젝트 폴더 루트 경로로 이동**


5. **프로젝트 빌드**

```bash
$ ./gradlew clean build -x test
```

6. **빌드 폴더 이동 후 jar 파일 실행**

```bash
$ cd build/libs
$ java -jar [파일명].jar
```

<br />

