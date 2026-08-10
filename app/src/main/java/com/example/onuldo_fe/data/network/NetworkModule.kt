package com.example.onuldo_fe.data.network

import android.content.Context
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


    private const val TIMEOUT_SECONDS = 30L

    /**
     * 토큰 재발급 호출 전체에 대한 상한. 스플래시가 이 시간 이상 유지되지 않도록 한다.
     * 정상 응답은 1초 안팎이라 여유가 충분하며, 초과해도 세션은 지워지지 않는다.
     */
    private const val REFRESH_CALL_TIMEOUT_SECONDS = 8L

    /**
     * 화면·Repository가 로그인 결과를 반영할 때 쓰는 토큰 저장소.
     *
     * 실제 구현은 [initialize]에서 갈아끼우지만, 이 참조 자체는 항상 같은 객체로 유지된다.
     * [okHttpClient]가 `lazy`로 이 값을 한 번 붙잡기 때문에, 여기서 구현을 직접 바꾸면
     * 초기화 시점에 따라 옛 저장소를 계속 쓰게 된다.
     */
    val tokenStore: TokenStore = DelegatingTokenStore

    /**
     * 앱 시작 시 한 번 호출해 토큰 저장소를 **영속 구현으로 교체**한다([OnuldoApplication]).
     * 호출하지 않으면 메모리 저장소로 동작하므로, 테스트·프리뷰는 이 호출 없이도 돌아간다.
     */
    fun initialize(context: Context) {
        DelegatingTokenStore.delegate = PersistentTokenStore.create(context)
    }

    /** 토큰 재발급 API. 자동 로그인(세션 복구)에서도 이 경로를 쓴다. */
    val tokenRefreshApi: TokenRefreshApi get() = refreshApi

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
     *
     * 재발급은 **앱 시작 시 스플래시를 붙잡는 경로**(세션 복구)이기도 해서, 전체 호출 시간에
     * 별도 상한([REFRESH_CALL_TIMEOUT_SECONDS])을 둔다. 공통 타임아웃은 연결·읽기·쓰기가 각각
     * 30초라 최악의 경우 스플래시가 1분 가까이 유지될 수 있다.
     *
     * `callTimeout`은 연결·전송·수신을 모두 합친 시간을 제한하며 초과 시 `InterruptedIOException`
     * (IOException)을 던진다. [executeRefresh]가 이를 `Transient`로 처리하므로 **세션은 보존**되고,
     * 남은 액세스 토큰의 유효기간으로 진입 화면이 정해진다.
     */
    private val refreshClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .applyTimeouts()
            .callTimeout(REFRESH_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            // 리디렉션을 아예 따라가지 않는다. `followSslRedirects(false)`는 HTTPS↔HTTP 전환만 막고,
            // `followRedirects`는 기본값이 true라 **다른 HTTPS 호스트**의 307/308은 그대로 따라간다.
            // 이때 OkHttp는 메서드와 본문을 유지하므로 **리프레시 토큰이 담긴 POST 본문이 그 호스트로 전달된다.**
            // (교차 호스트에서 떨어지는 건 `Authorization` 헤더뿐이라 본문은 보호되지 않는다.)
            // 우리 API는 리디렉션을 쓰지 않으므로 끄더라도 정상 동작에 영향이 없다.
            .followRedirects(false)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val refreshApi: TokenRefreshApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
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
            .baseUrl(BuildConfig.API_BASE_URL)
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

/**
 * 실제 저장소로 넘겨주기만 하는 껍데기.
 *
 * [NetworkModule.initialize]가 앱 시작 시 [delegate]를 영속 구현으로 바꾼다.
 * 인터셉터·Authenticator가 붙잡는 참조는 이 객체라서, 교체 시점과 무관하게 항상 최신 저장소를 본다.
 */
private object DelegatingTokenStore : TokenStore {

    @Volatile
    var delegate: TokenStore = InMemoryTokenStore

    override val accessToken: String? get() = delegate.accessToken
    override val refreshToken: String? get() = delegate.refreshToken

    override fun update(tokens: AuthTokens) = delegate.update(tokens)
    override fun clear() = delegate.clear()
}
