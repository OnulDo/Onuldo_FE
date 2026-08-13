package com.example.onuldo_fe.ui.screen.party.components

import androidx.annotation.DrawableRes
import com.example.onuldo_fe.R

// API의 기본 캐릭터 ID를 앱 drawable 리소스로 변환
@DrawableRes
internal fun partyCharacterDrawable(defaultCharacterId: Int?): Int = when (defaultCharacterId) {
    1 -> R.drawable.ic_char_01
    2 -> R.drawable.ic_char_02
    3 -> R.drawable.ic_char_03
    4 -> R.drawable.ic_char_04
    5 -> R.drawable.ic_char_05
    6 -> R.drawable.ic_char_06
    7 -> R.drawable.ic_char_07
    8 -> R.drawable.ic_char_08
    9 -> R.drawable.ic_char_09
    else -> R.drawable.party_member_avatar
}
