package com.example.onuldo_fe.data.network

import com.example.onuldo_fe.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 앱 공통 네트워크 계층.
 *
 * DI 라이브러리를 쓰지 않는 프로젝트라, 기존 `*RepositoryProvider` 패턴과 같은 결로
 * object + lazy 조립을 사용한다. API 인터페이스는 [create]로 만든다.
 *
 * ```
 * private val authApi = NetworkModule.create(AuthApi::class.java)
 * ```
 */
object NetworkModule {

    const val BASE_URL = "https://onuldo.site/"

    private const val TIMEOUT_SECONDS = 30L

    /** 화면·Repository가 로그인 결과를 반영할 때 쓰는 토큰 저장소. */
    val tokenStore: TokenStore = InMemoryTokenStore

    /** 디버그에서는 요청 정보만 기록하고 인증 헤더는 마스킹한다. 릴리스에서는 로깅하지 않는다. */
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            level = if (BuildConfig.DEBUG) {
                // 로그인 응답 및 토큰 재발급 요청 본문에도 토큰이 있으므로 BODY는 기록하지 않는다.
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * 토큰 재발급 전용 클라이언트.
     * [TokenAuthenticator]가 붙어 있지 않아, 재발급 요청이 401을 받아도 재귀하지 않는다.
     */
    private val refreshClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .applyTimeouts()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val refreshApi: TokenRefreshApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(refreshClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenRefreshApi::class.java)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .applyTimeouts()
            .addInterceptor(AuthInterceptor(tokenStore))
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenAuthenticator(tokenStore) { refreshApi })
            .build()
    }

    /** 공통 Retrofit 인스턴스. 직접 쓰기보다 [create]를 권장하며, [RetrofitClient]가 이 값을 노출한다. */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    /**
     * 공통 설정. [followSslRedirects]를 끄는 이유는, 서버가 307/308로 HTTP 주소를 돌려줄 경우
     * 비밀번호·소셜 토큰·리프레시 토큰이 담긴 POST 본문이 평문으로 재전송될 수 있기 때문이다.
     */
    private fun OkHttpClient.Builder.applyTimeouts(): OkHttpClient.Builder =
        connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .followSslRedirects(false)
}
