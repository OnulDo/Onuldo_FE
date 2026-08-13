package com.example.onuldo_fe.data.network

import retrofit2.Retrofit

/**
 * 공통 Retrofit 인스턴스 접근점.
 *
 * 챌린지 등 다른 기능이 `RetrofitClient.retrofit`이라는 이름으로 참조하고 있어 그 이름에 맞춘다.
 * 실제 조립은 [NetworkModule]이 담당하며, 여기서 얻는 인스턴스에는 인증 헤더 부착과
 * 액세스 토큰 자동 재발급(401 → refresh → 재시도)이 모두 적용돼 있다.
 *
 * 새로 작성하는 코드는 `NetworkModule.create(XxxApi::class.java)`를 쓰는 편이 간결하다.
 */
object RetrofitClient {

    val retrofit: Retrofit
        get() = NetworkModule.retrofit
}
