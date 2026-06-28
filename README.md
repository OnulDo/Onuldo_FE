## **오늘두, 미루던 습관을 오늘 해내게 만드는, 도전금 기반 AI 인증 챌린지**

## 👥 팀원 소개

| 이름 | 역할 | 담당 기능 |
| --- | --- | --- |
| 하연탄 | Front-End | 카메라, 인증 |
| 시온 | Front-End | 챌린지 |
| 조나단 | Front-End | 로그인, 회원가입, 홈 |
| 구민 | Front-End | 마이페이지, 파티 |

## 🛠 기술 스택

- **Language**: Kotlin
- **Architecture**: MVVM
- **UI**: Jetpack Compose
- **Asynchronous**: Kotlin Coroutines
- **Image Loading**: Glide
- **Camera**: CameraX

## 📂 프로젝트 구조

```
app
├── ui                 # 화면 및 UI 컴포넌트
│   ├── screen
│   ├── component
│   └── theme
├── viewmodel          # UI 상태 관리
├── repository         # 데이터 처리
├── model              # 데이터 모델
├── navigation         # 화면 이동 관리
├── camera             # CameraX 관련 기능
└── utils              # 공통 유틸리티
```

- 빌드 및 실행 방법

에뮬레이터 또는 Android 기기를 연결한 후 **Run** 버튼을 눌러 실행합니다.

## 📖 Convention

### 🌱 Git Branch

- `main` : 배포 브랜치
- `develop` : 개발 브랜치

### 💬 Commit Message

| Type | Description |
| --- | --- |
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩토링 |
| `style` | 코드 스타일 변경 (기능 변경 없음) |
| `design` | UI 디자인 변경 |
| `docs` | 문서 수정 |
| `chore` | 빌드 및 설정 변경 |
| `test` | 테스트 코드 작성 |
- 화면 목록 & 플로우 정리

| 화면이름 | 스크린 ID | 담당자 |
| --- | --- | --- |
| 로그인 | LoginScreen | 조나단 |
| 회원가입 | SignUpScreen | 조나단 |
| 인증 메일 발송 | EmailScreen | 하연탄 |
| 홈  | HomeScreen | 조나단 |
| 챌린지  | ChallengeScreen | 시온 |
| 인증 | ChallengeVerificationScreen | 하연탄 |
| 마이페이지 | MyPageScreen | 구민 |
| 파티 | PartyScreen | 구민 |

## 🔀 Pull Request

- PR 생성 전 `develop` 브랜치의 최신 내용을 반영합니다.
- PR 제목은 작업 내용을 명확하게 작성합니다.
- 최소 1명 이상의 팀원 리뷰를 받은 후 머지합니다.
- 충돌(Conflict)이 없는 상태에서 머지를 진행합니다.
- PR 본문에는 작업 내용 및 변경 사항을 작성합니다.

## 📝 Code Naming

| 대상 | 규칙 | 예시 |
| --- | --- | --- |
| Class | PascalCase | `LoginViewModel` |
| Composable | PascalCase | `HomeScreen()` |
| Function | camelCase | `loadUserInfo()` |
| Variable | camelCase | `userName` |
| Constant | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Package | lowercase | `ui.screen.home` |

## 🔄 화면 플로우

```
로그인
 ├── 회원가입
 │     └── 인증 메일 발송
 └── 홈
       ├── 챌린지
       │      └── 인증
       ├── 마이페이지
       └── 파티
```
