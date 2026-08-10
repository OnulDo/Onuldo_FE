import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

/**
 * API 테스트 값과 소셜 로그인 키는 Git에서 제외되는 local.properties에서 읽는다.
 * 값이 없으면 빈 문자열을 사용해 키가 없는 팀원도 프로젝트를 빌드할 수 있게 한다.
 */
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val envProperties = Properties().apply {
    val file = rootProject.file(".env")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun env(key: String): String =
    envProperties.getProperty(key)
        ?: error("Missing required environment variable: $key")

fun apiBaseUrl(): String {
    val value = env("API_BASE_URL").trim()

    require(value.isNotEmpty()) {
        "API_BASE_URL must not be blank."
    }
    require(value.startsWith("http://") || value.startsWith("https://")) {
        "API_BASE_URL must start with http:// or https://"
    }
    require(value.endsWith("/")) {
        "API_BASE_URL must end with '/'."
    }

    return value
}

fun secret(key: String): String = localProperties.getProperty(key).orEmpty()

fun quotedBuildConfig(value: String): String =
    "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

android {
    namespace = "com.example.onuldo_fe"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.onuldo_fe"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "API_BASE_URL",
            quotedBuildConfig(apiBaseUrl())
        )
        val kakaoNativeAppKey = secret("KAKAO_NATIVE_APP_KEY")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", quotedBuildConfig(kakaoNativeAppKey))
        buildConfigField("String", "NAVER_CLIENT_ID", quotedBuildConfig(secret("NAVER_CLIENT_ID")))
        buildConfigField("String", "NAVER_CLIENT_SECRET", quotedBuildConfig(secret("NAVER_CLIENT_SECRET")))

        // 카카오톡 앱 로그인 결과를 돌려받는 커스텀 스킴(kakao{네이티브앱키}).
        manifestPlaceholders["kakaoNativeAppKey"] = kakaoNativeAppKey
    }

    /**
     * 디버그 서명을 팀 공용 키스토어로 고정한다(`keystore/debug.keystore`, 저장소에 포함).
     *
     * 기본값인 `~/.android/debug.keystore`는 PC마다 자동 생성돼 서명이 전부 달라진다.
     * 그러면 카카오 콘솔에 팀원 수만큼 키 해시를 등록해야 하고, 새 팀원이 올 때마다
     * 관리자가 콘솔을 다시 열어야 한다. 공용 키로 고정하면 키 해시는 하나면 된다.
     *
     * 비밀번호가 안드로이드 기본 관례값(`android`)인 **디버그 전용** 키다.
     * `getByName("debug")`만 재정의하므로 `release` 변형에는 영향이 없다
     * (`:app:signingReport` 기준 release = `Config: none`).
     * 릴리스 키는 절대 저장소에 넣지 않으며, 아래 [buildTypes] 주석을 함께 볼 것.
     */
    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            // 릴리스 서명은 의도적으로 비워 둔다. 위 공용 키스토어는 디버그 전용이므로
            // 여기에 signingConfig를 연결하면 안 된다. 출시용 키는 저장소 밖에서
            // 따로 관리하고(로컬 keystore.properties 또는 CI 시크릿),
            // 그때 카카오·네이버 콘솔에서 디버그 키 해시를 제거한다.
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests {
            // 단위 테스트에서 android.util.Log 등 프레임워크 호출이 예외를 던지지 않게 한다.
            // (기본값은 "not mocked" 예외 → 로그 한 줄 때문에 순수 로직 테스트가 깨진다.)
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    // 자동 로그인용 토큰 암호화 저장 (EncryptedSharedPreferences)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    // Firebase Cloud Messaging (FCM) — BoM으로 버전 통일
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.okhttp.logging.interceptor)
    implementation("androidx.exifinterface:exifinterface:1.4.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation(libs.kakao.user)
    implementation(libs.naver.oauth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
