package com.yohai.mycoffee

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedCommonTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun settingsHaveExpectedFirstLaunchDefaults() {
        val defaults = com.yohai.mycoffee.database.Settings.DEFAULT
        assertEquals(true, defaults.useGrams)
        assertEquals(340.0, defaults.defaultBagSize)
        assertEquals(null, defaults.darkMode)
        assertEquals(com.yohai.mycoffee.database.BrewMethod.ESPRESSO, defaults.defaultBrewMethod)
        assertEquals(18.0, defaults.defaultBrewDose)
        assertEquals(36.0, defaults.defaultBrewYield)
    }
}
