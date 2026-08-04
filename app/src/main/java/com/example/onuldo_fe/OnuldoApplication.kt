package com.example.onuldo_fe

import android.app.Application
import com.example.onuldo_fe.data.social.SocialAuthClient

/**
 * 앱 진입점. 소셜 로그인 SDK는 앱 시작 시 한 번만 초기화하면 되므로 여기서 처리한다.
 *
 * 키가 없는 환경(팀원이 `local.properties`에 키를 넣지 않은 경우)에서도 앱은 정상 실행되고,
 * 소셜 로그인 버튼만 동작하지 않는다.
 */
class OnuldoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SocialAuthClient.initialize(this)
    }
}
