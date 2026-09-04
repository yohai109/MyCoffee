package com.yohai.mycoffee

import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Android-specific UI tests
 * These are basic smoke tests to ensure Android-specific code compiles and integrates correctly
 */
class AndroidAppTest {

    @Test
    fun testAndroidPlatformString() {
        // Basic smoke test to ensure Android test infrastructure works
        val platform = "Android"
        assertNotNull(platform)
    }

    @Test
    fun testAndroidMainActivityClassExists() {
        // Verify MainActivity class exists in the Android-specific source
        val mainActivityClass = try {
            Class.forName("com.yohai.mycoffee.MainActivity")
        } catch (e: ClassNotFoundException) {
            null
        }
        assertNotNull(mainActivityClass, "MainActivity should exist in Android source")
    }
}
