package com.yohai.mycoffee

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComposeAppCommonTest {

    @Test
    fun testBasicArithmetic() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun testScreenRoutesAreDefined() {
        // Test that all screen routes are properly defined
        assertEquals("stock", Screen.Stock.route)
        assertEquals("brew", Screen.Brew.route)
        assertEquals("settings", Screen.Settings.route)
    }

    @Test
    fun testScreenLabelsAreDefined() {
        // Test that all screen labels are properly defined
        assertEquals("Stock", Screen.Stock.label)
        assertEquals("Brew", Screen.Brew.label)
        assertEquals("Settings", Screen.Settings.label)
    }

    @Test
    fun testScreenIconsAreNotNull() {
        // Test that all screen icons are defined
        assertTrue(Screen.Stock.icon != null, "Stock icon should be defined")
        assertTrue(Screen.Brew.icon != null, "Brew icon should be defined")
        assertTrue(Screen.Settings.icon != null, "Settings icon should be defined")
    }
}
