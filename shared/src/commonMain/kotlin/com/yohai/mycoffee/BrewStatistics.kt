package com.yohai.mycoffee

import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import kotlinx.datetime.LocalDate

fun extractionRatio(brew: BrewRecord): Double? = brew.yield?.takeIf { brew.dose > 0 }?.div(brew.dose)
fun averageExtractionRatio(brews: List<BrewRecord>): Double? = brews.mapNotNull(::extractionRatio).takeIf { it.isNotEmpty() }?.average()
fun favoriteBrewMethod(brews: List<BrewRecord>): BrewMethod? = brews.groupingBy { it.method }.eachCount().maxByOrNull { it.value }?.key
fun filterBrews(brews: List<BrewRecord>, method: BrewMethod? = null, from: LocalDate? = null, to: LocalDate? = null, coffeeId: Long? = null) = brews.filter { method == null || it.method == method }.filter { from == null || it.date >= from }.filter { to == null || it.date <= to }.filter { coffeeId == null || it.coffeeStockId == coffeeId }
fun brewsByMonth(brews: List<BrewRecord>) = brews.groupingBy { it.date.toString().take(7) }.eachCount()
