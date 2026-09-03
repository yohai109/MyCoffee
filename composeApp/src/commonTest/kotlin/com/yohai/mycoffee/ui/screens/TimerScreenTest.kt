package com.yohai.mycoffee.ui.screens

import kotlin.test.Test
import kotlin.test.assertEquals

class TimerScreenTest {
    @Test
    fun timerDurationMillisAcceptsOnlyPositiveWholeSeconds() {
        assertEquals(60_000L, timerDurationMillis("60"))
        assertEquals(0L, timerDurationMillis("0"))
        assertEquals(0L, timerDurationMillis("bad"))
        assertEquals(0L, timerDurationMillis(""))
    }
}
