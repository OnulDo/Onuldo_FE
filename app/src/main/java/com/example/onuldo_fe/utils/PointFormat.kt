package com.example.onuldo_fe.utils

import java.text.NumberFormat
import java.util.Locale

/** 포인트 표기: `52000` → `"52,000P"`. */
fun formatPoint(point: Long): String = "${formatAmount(point)}P"

/** 금액 표기(단위 없음): `52000` → `"52,000"`. 음수도 부호를 유지한다. */
fun formatAmount(amount: Long): String =
    NumberFormat.getNumberInstance(Locale.KOREA).format(amount)

fun formatAmount(amount: Int): String = formatAmount(amount.toLong())

/** 부호를 항상 붙인 금액 표기: `+5,000` / `-1,000`. 거래내역에 쓴다. */
fun formatSignedAmount(amount: Int): String =
    if (amount >= 0) "+${formatAmount(amount)}" else formatAmount(amount)
