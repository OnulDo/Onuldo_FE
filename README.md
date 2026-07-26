## **오늘두, 미루던 습관을 오늘 해내게 만드는, 도전금 기반 AI 인증 챌린지**

## 👥 팀원 소개

| 이름 | 역할 | 담당 기능 |
| --- | --- | --- |
| 하연탄 | Front-End | 카메라, 인증, 챌린지 참여 |
| 시온 | Front-End | 챌린지페이지, 알림 |
| 조나단 | Front-End | 로그인, 회원가입, 마이페이지 |
| 구민 | Front-End | 홈, 파티|

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

## 빌드 및 실행 방법

에뮬레이터 또는 Android 기기를 연결한 후 **Run** 버튼을 눌러 실행합니다.

## 📖 Convention

### 🌱 Git Branch

- `main` : 배포 브랜치
- `develop` : 개발 브랜치
- `feature/{기능명}` : 새로운 기능 개발을 위한 브랜치

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

## 화면 목록 & 플로우 정리

| 화면이름 | 스크린 ID | 담당자 |
| --- | --- | --- |
| 로그인 | LoginScreen | 조나단 |
| 회원가입 | SignUpScreen | 조나단 |
| 홈  | HomeScreen | 구민 |
| 챌린지  | ChallengeScreen | 시온 |
| 인증 | ChallengeVerificationScreen | 하연탄 |
| 마이페이지 | MyPageScreen | 조나단 |
| 파티 | PartyScreen | 구민 |

## 🔀 Pull Request

- PR 생성 전 `develop` 브랜치의 최신 내용을 반영합니다.
- PR 제목은 작업 내용을 명확하게 작성합니다.
- PR 본문에는 작업 내용 및 변경 사항을 작성합니다.
- 모든 PR은 최소 1명 이상의 팀원에게 코드 리뷰를 받은 후 Merge합니다.
- 공통 컴포넌트, 아키텍처 변경, 대규모 리팩토링 등 프로젝트 전반에 영향을 주는 변경사항은 2명 이상의 리뷰를 권장합니다.
- PR 작성자는 본인의 PR을 직접 Merge하지 않고, 리뷰어가 Merge합니다.
- 충돌(Conflict)이 없는 상태에서 Merge를 진행합니다.

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
## 피그마 링크 

https://www.figma.com/design/Cnn54WuPxboHl6XAieA1BW/%EC%98%A4%EB%8A%98%EB%91%90?node-id=4516-5&t=6sAc148DgwkqwoyS-4

## 구현 화면 목록

### 온보딩 / 로그인

- `LandingScreen`
    - 앱 진입 랜딩 화면
- `LoginScreen`
    - 로그인 화면
- `SignupScreen`
    - 회원가입 화면
- `ProfileSetupScreen`
    - 프로필 설정 화면
- `WelcomeScreen`
    - 가입 완료 후 환영 화면

### 홈

- `HomeScreen`
    - 홈 메인 화면
- `NotificationScreen`
    - 알림 화면
- `HomeRoute`
    - 홈/알림 전환 및 인증하기 진입 처리

### 파티

- `PartyListScreen`
    - 파티 목록 화면
- `PartyCreateScreen`
    - 파티 생성 화면
- `PartyWaitingRoomScreen`
    - 파티 대기방 화면
- `PartyFeedScreen`
    - 파티 인증 피드 화면
- `PartySettlementScreen`
    - 파티 정산 화면
- `PartyRoute`
    - 파티 내부 화면 전환 및 상태 관리

### 챌린지

- `GalleryScreen`
    - 챌린지 목록/탐색 화면
- `DetailScreen`
    - 챌린지 상세 화면
- `ParticipateScreen`
    - 챌린지 참여 화면
- `StartDoneScreen`
    - 챌린지 시작 완료 화면
- `VerificationNoticeBottomSheet`
    - 인증 유의사항 바텀시트(카메라Part 재활용)

### 인증 / 카메라

- `CameraPermissionScreen`
    - 카메라 권한 안내 화면
- `CameraScreen`
    - 카메라 촬영 화면
- `PhotoPreviewScreen`
    - 촬영 사진 미리보기 화면
- `ChallengeVerificationScreen`
    - 인증 결과 분기 화면 (검토 중 화면 고정)
- `VerificationWaitingScreen`
    - 인증 검토 대기 화면
- `VerificationReviewingScreen`
    - 인증 검토 중 화면
- `VerificationSuccessScreen`
    - 인증 성공 화면
- `VerificationFailureScreen`
    - 인증 실패 화면

### 파티

- `PartyListScreen`
    - 파티 목록 화면
- `PartyCreateScreen`
    - 파티 생성 화면
- `PartyWaitingRoomScreen`
    - 파티 대기방 화면
- `PartyFeedScreen`
    - 파티 인증 피드 화면
- `PartySettlementScreen`
    - 파티 정산 화면
- `PartyRoute`
    - 파티 내부 화면 전환 및 상태 관리

### 기록

- `RecordScreen`
    - 기록 메인 화면
- `ProgressRecordScreen`
    - 진행 중 기록 화면
- `CompleteRecordScreen`
    - 완료 기록 화면
- `EmptyRecordView`
    - 기록이 없을 때의 빈 화면

### 마이페이지

- `MyMainScreen`
    - 마이페이지 메인 화면
- `ProfileSettingsScreen`
    - 프로필 설정 화면
- `NicknameEditScreen`
    - 닉네임 수정 화면
- `PasswordChangeScreen`
    - 비밀번호 변경 화면
- `NotificationSettingScreen`
    - 알림 설정 화면
- `PointWalletScreen`
    - 포인트 지갑 화면
- `PointChargeScreen`
    - 포인트 충전 화면
- `PointWithdrawScreen`
    - 포인트 출금 화면
- `WithdrawAccountScreen`
    - 계좌/출금 관련 화면
