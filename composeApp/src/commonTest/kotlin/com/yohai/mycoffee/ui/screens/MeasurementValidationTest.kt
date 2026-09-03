package com.yohai.mycoffee.ui.screens

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MeasurementValidationTest {
    @Test
    fun displayFormattingRoundsOuncesAndKeepsWholeGramsReadable() {
        assertEquals("250", formatDisplayMeasurement(250.0, true))
        assertEquals("0.63", formatDisplayMeasurement(18.0, false))
        assertEquals("10.3", formatDisplayMeasurement(292.0, false))
    }

    @Test
    fun validationCoversBlankMalformedRangeAndValidValues() {
        assertEquals("Dose is required", measurementError("", "Dose", 0.1, 1000.0))
        assertEquals("Enter a number", measurementError("bad", "Dose", 0.1, 1000.0))
        assertEquals("Enter 0.1 to 1000.0", measurementError("0", "Dose", 0.1, 1000.0))
        assertNull(measurementError("18", "Dose", 0.1, 1000.0))
        assertNull(optionalMeasurementError("", "Yield", 0.1, 5000.0))
    }
}
