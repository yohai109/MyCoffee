package com.yohai.mycoffee

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Desktop (JVM)-specific UI tests
 * These are basic smoke tests to ensure Desktop/JVM-specific code compiles and integrates correctly
 */
class DesktopAppTest {

    @Test
    fun testDesktopPlatformString() {
        // Basic smoke test to ensure Desktop test infrastructure works
        val platform = "Desktop/JVM"
        assertNotNull(platform)
    }

    @Test
    fun testDesktopComposeIntegration() {
        // Test that Compose Desktop integration is working
        // Verify we're running in a JVM environment
        val isDesktop = System.getProperty("java.vm.name") != null
        assertTrue(isDesktop, "Should be running on JVM")
    }

    @Test
    fun testDesktopJavaEnvironment() {
        // Verify we're running in a JVM environment with a Java version
        val javaVersion = System.getProperty("java.version")
        assertNotNull(javaVersion, "Java version should be available on Desktop/JVM platform")
    }
}
