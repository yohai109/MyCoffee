package com.yohai.mycoffee.ui.screens

private const val GRAMS_PER_OUNCE = 28.3495

fun gramsToOunces(grams: Double): Double = grams / GRAMS_PER_OUNCE

fun ouncesToGrams(ounces: Double): Double = ounces * GRAMS_PER_OUNCE

fun formatDisplayMeasurement(grams: Double, useGrams: Boolean): String {
    val value = if (useGrams) grams else gramsToOunces(grams)
    val rounded = kotlin.math.round(value * 100) / 100
    return rounded.toString().removeSuffix(".0")
}

fun formatMeasurement(grams: Double, useGrams: Boolean): String =
    formatDisplayMeasurement(grams, useGrams)

fun measurementError(text: String, label: String, min: Double, max: Double): String? {
    if (text.isBlank()) return "$label is required"
    val value = text.toDoubleOrNull() ?: return "Enter a number"
    return if (value in min..max) null else "Enter $min to $max"
}

fun optionalMeasurementError(text: String, label: String, min: Double, max: Double): String? {
    if (text.isBlank()) return null
    val value = text.toDoubleOrNull() ?: return "Enter a number"
    return if (value in min..max) null else "Enter $min to $max"
}

fun integerError(text: String, label: String, min: Int, max: Int, required: Boolean): String? {
    if (text.isBlank()) return if (required) "$label is required" else null
    val value = text.toIntOrNull() ?: return "Enter a whole number"
    return if (value in min..max) null else "Enter $min to $max"
}
