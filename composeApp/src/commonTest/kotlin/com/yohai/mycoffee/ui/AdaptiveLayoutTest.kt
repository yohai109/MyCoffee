package com.yohai.mycoffee.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class AdaptiveLayoutTest {
    @Test
    fun widthMapsToCentralizedLayoutClass() {
        assertEquals(WindowLayoutClass.COMPACT, windowLayoutClassForWidth(599))
        assertEquals(WindowLayoutClass.MEDIUM, windowLayoutClassForWidth(600))
        assertEquals(WindowLayoutClass.EXPANDED, windowLayoutClassForWidth(840))
    }
}
