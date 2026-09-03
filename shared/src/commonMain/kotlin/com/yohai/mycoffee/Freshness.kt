package com.yohai.mycoffee

import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

enum class Freshness { FRESH, PEAK, AGING, PAST_OPTIMAL }
fun freshnessFor(roastDate: LocalDate, today: LocalDate) = when ((today.toEpochDays() - roastDate.toEpochDays()).coerceAtLeast(0).toInt()) {
    in 0..6 -> Freshness.FRESH
    in 7..20 -> Freshness.PEAK
    in 21..30 -> Freshness.AGING
    else -> Freshness.PAST_OPTIMAL
}
fun bestBeforeDate(roastDate: LocalDate) = roastDate.plus(28, DateTimeUnit.DAY)
