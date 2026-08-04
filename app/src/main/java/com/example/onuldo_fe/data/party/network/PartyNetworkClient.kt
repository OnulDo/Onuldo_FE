package com.example.onuldo_fe.data.party.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** 파티 실제 API 연동용 임시 네트워크 클라이언트. 공통 네트워크 계층 도입 후 교체한다. */
object PartyNetworkClient {
    private const val BASE_URL = "https://onuldo.site/"

    private val authInterceptor = Interceptor { chain ->
        val token = PartyAccessTokenStore.get()
        val request = if (token == null) {
            chain.request()
        } else {
            chain.request()
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
        chain.proceed(request)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
