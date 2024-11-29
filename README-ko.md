# Project LCS: 오픈 소스 AI agent

[English](README.md)

**LCS**는 ChatGPT에 기반한 오픈 소스 AI 에이전트입니다.

---

## Why LCS?

1. **확장성**: 개발자가 손쉽게 새로운 기능을 추가할 수 있습니다.
2. **AI 개선**: 쉽게 AI를 커스터마이징하고 개선할 수 있습니다.
3. **오픈 소스**: 누구나 자유롭게 코드를 볼 수 있습니다!

---

## 작동 원리

1. 사용자가 프롬프트를 입력하면, LCS가 해당 프롬프트와 기존 함수 정보를 ChatGPT에 전송합니다.
2. ChatGPT가 프롬프트를 수행하는 적절한 Lua 코드를 생성합니다.
3. LCS는 ChatGPT가 생성한 함수를 실행합니다.

---

### LCS가 다른 AI 에이전트보다 뛰어난 이유

- **백그라운드 프로세스 지원**: LCS는 지속적인 백그라운드 작업을 생성할 수 있습니다.  
  `집에 도착하면 방해 금지 모드를 해제하세요.` 같은 명령어도 처리할 수 있죠
- **높은 확장성**: 개발자가 새로운 기능을 구현하려면 객체에 Annotation을 추가하고, JavaDoc 형식으로 설명만 제공하면 됩니다.

---

## Demo

[Youtube](https://www.youtube.com/watch?v=kIjUqxy436o)

---

## Contribution

PR을 자유롭게 열어 주세요! 제가 이 프로젝트를 적극적으로 관리하고 있진 않지만, 최선을 다해 PR을 검토할꺼에요.

---

## 빠른 개요

각 기능별 코드가 위치한 대략적인 페키지입니다:

| **기능**              | **패키지**                                                            |  
|---------------------|--------------------------------------------------------------------|  
| AI 관련 기능            | `net.projectlcs.lcs.ai`                                            |  
| 구현된 기능              | `net.projectlcs.lcs.functions.impl`                                |  
| UI                  | `net.projectlcs.lcs.MainActivity`, `net.projectlcs.lcs.permission` |  
| Script data handler | `net.projectlcs.lcs.data`                                          |  

> Lua 엔진 관련 내부 작업은 [Aris.luagen](https://github.com/dayo05/aris.luagen)을 참조하세요.

---

## 구현된 기능

| **기능**           | **객체**            | **상태** |  
|------------------|-------------------|--------|  
| Android 기본 알람 생성 | `Alarms`          | ✔      |  
| 날짜 및 시간          | `DateTime`        | ✔      |  
| 다이얼로그            | `Dialog`          | ✔      |  
| 방해 금지 모드 설정      | `DND`             | ✔      |  
| 파일 관리            | `FileManagement`  | ✔      |  
| 위치 서비스           | `Location`        | ✔      |  
| 네트워킹             | `Network`         | ✔      |  
| 알림 전송            | `Notification`    | ✔      |  
| 패키지명으로 애플리케이션 열기 | `OpenApplication` | ✔      |  
| 한국 지하철 API       | `SubwayApiCall`   | ✔      |  
| 네트워크 활성/비활성화     | `N/A`             | TODO   |  

> 추가 기능 요청은 이슈를 열거나 PR로 알려주세요!

---

## UI

앱의 UI/UX가 조금 구려요 ㅋㅋㅋ... 좋은 아이디어가 있다면, 이슈를 열어 제안해 주시면 좋을꺼 같아요!

---

## TODO

- 기능 추가
- Siri나 Bixby처럼 목소리로 프롬프트 입력하는 기능
- AIDL을 활용한 IPC(프로세스 간 통신) 구현으로 앱 확장성 강화

이와 관련된 PR은 다른 PR들보다 우선해서 확인할 계획입니다 :)

---

## Contact us

### 프로젝트 총괄: **김지민 (@dayo05)**

- 이메일: dayo05@daum.net
- 이메일: dayo@ajou.ac.kr

### UI 및 기능 담당: **서진형 (@seojinhyeong99)**

### AI 개발 담당: **남민우 (@brainVRG)**

- 이메일: woo95822@gnu.ac.kr  