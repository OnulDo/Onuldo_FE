package com.example.onuldo_fe.repository.verification

import android.content.Context
import com.example.onuldo_fe.BuildConfig
import com.example.onuldo_fe.data.verification.api.VerificationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VerificationRepositoryProvider {
    fun create(context: Context): VerificationRepository {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = BuildConfig.DEV_ACCESS_TOKEN.trim()
                val request = chain.request().newBuilder().apply {
                    if (token.isNotEmpty()) header("Authorization", "Bearer $token")
                }.build()
                chain.proceed(request)
            }
            .apply {
                if (BuildConfig.DEBUG) addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
            .build()
        val api = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VerificationApi::class.java)
        return VerificationRepositoryImpl(context.applicationContext, api)
    }
}
