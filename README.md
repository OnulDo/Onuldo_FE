## **오늘두, 미루던 습관을 오늘 해내게 만드는, 도전금 기반 AI 인증 챌린지**

## 👥 팀원 소개

| 이름 | 역할 | 담당 기능 |
| --- | --- | --- |
| 하연탄 | Front-End | 카메라, 인증, 챌린지 참여 |
| 시온 | Front-End | 챌린지페이지, 알림 |
| 조나단 | Front-End | 로그인, 회원가입, 마이페이지 |
| 구민 | Front-End | 홈, 파티 |

## 🛠 기술 스택

- **Language**: Kotlin
- **Architecture**: MVVM
- **UI**: Jetpack Compose
- **Asynchronous**: Kotlin Coroutines
- **Image Loading**: Coil
- **Camera**: CameraX

## 📂 프로젝트 구조

```
## 📂 프로젝트 구조

```text
app/src/main/java/com/example/onuldo_fe
├── data               # API, DTO, 네트워크 및 로컬 데이터 관리
│   ├── auth
│   ├── challenge
│   ├── common
│   ├── home
│   ├── network
│   ├── notification
│   ├── party
│   ├── record
│   ├── social
│   ├── term
│   ├── user
│   └── verification
├── model              # 도메인 데이터 모델
├── navigation         # 화면 이동 및 라우트 관리
├── repository         # 데이터 접근 및 처리
├── service            # Firebase 등 백그라운드 서비스
├── ui                 # 화면 및 UI 컴포넌트
│   ├── component      # 공통 UI 컴포넌트
│   ├── screen         # 기능별 화면
│   │   └── camera     # CameraX 촬영 및 미리보기 화면
│   └── theme          # 색상, 글꼴 및 디자인 테마
├── util               # 권한 설정 관련 유틸리티
├── utils              # 검증, 포맷팅 등 공통 유틸리티
└── viewmodel          # UI 상태 및 사용자 이벤트 관리
```

## 빌드 및 실행 방법

### 1. 프로젝트 실행 환경

- Android Studio
- JDK 11
- Android SDK 36
- Android 8.0(API 26) 이상의 에뮬레이터 또는 실제 기기

### 2. 환경 설정 파일

프로젝트 실행에 필요한 다음 설정 파일을 준비합니다.

- `.env`
- `local.properties`
- `app/google-services.json`

설정 파일과 필요한 값은 프로젝트 관리자에게 별도로 전달받아 지정된 위치에 추가합니다.

> 환경 설정 파일에는 민감한 정보가 포함될 수 있으므로 Git에 커밋하지 않습니다.
> 

### 3. 실행

1. Android Studio에서 프로젝트를 엽니다.
2. 필요한 환경 설정 파일을 추가합니다.
3. Gradle Sync를 실행합니다.
4. 에뮬레이터 또는 Android 기기를 연결합니다.
5. 상단의 **Run** 버튼을 눌러 앱을 실행합니다.

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
| 인증 메일 발송 | EmailScreen | 조나단 |
| 홈 | HomeScreen | 구민 |
| 챌린지 | ChallengeScreen | 시온 |
| 인증 | ChallengeVerificationScreen | 하연탄 |
| 마이페이지 | MyPageScreen | 조나단 |
| 파티 | PartyScreen | 구민 |
| 알림 | NotificationScreen | 시온 |

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
랜딩
├── 로그인
│   ├── 소셜 신규 가입
│   │   └── 약관 동의
│   └── 메인
└── 회원가입
    └── 약관 동의
        └── 프로필 설정
            └── 가입 완료
                └── 메인

메인
├── 홈
│   ├── 알림
│   └── 챌린지 인증
│       └── 카메라 촬영
│           └── 사진 미리보기
│               └── 인증 검토
│                   ├── 인증 성공
│                   ├── 인증 실패
│                   └── 인증 대기
├── 챌린지
│   └── 챌린지 상세
│       └── 챌린지 참여
│           └── 참여 완료
├── 파티
│   ├── 파티 생성
│   ├── 파티 대기방
│   ├── 파티 피드
│   └── 파티 정산
├── 기록
│   ├── 진행 중인 기록
│   └── 완료된 기록
└── 마이페이지
    ├── 프로필 설정
    │   └── 닉네임 변경
    ├── 포인트 지갑
    │   ├── 포인트 충전
    │   └── 포인트 출금
    ├── 알림 설정
    └── 약관
```
