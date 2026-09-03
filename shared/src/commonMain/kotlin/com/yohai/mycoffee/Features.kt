package com.yohai.mycoffee

import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeStock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

enum class Freshness { FRESH, PEAK, AGING, PAST_OPTIMAL, UNKNOWN }

fun daysSinceRoast(roastDate: LocalDate, today: LocalDate): Int =
    (today.toEpochDays() - roastDate.toEpochDays()).coerceAtLeast(0).toInt()

fun freshnessFor(roastDate: LocalDate?, today: LocalDate): Freshness = roastDate?.let {
    when (daysSinceRoast(it, today)) {
        in 0..6 -> Freshness.FRESH
        in 7..20 -> Freshness.PEAK
        in 21..30 -> Freshness.AGING
        else -> Freshness.PAST_OPTIMAL
    }
} ?: Freshness.UNKNOWN

fun bestBeforeDate(roastDate: LocalDate?, days: Int = 28): LocalDate? =
    roastDate?.plus(days, DateTimeUnit.DAY)

fun freshestFirst(stock: List<CoffeeStock>, today: LocalDate): List<CoffeeStock> =
    stock.sortedWith(compareBy { daysSinceRoast(it.roastDate, today) })

fun extractionRatio(brew: BrewRecord): Double? = brew.yield?.takeIf { brew.dose > 0 }?.div(brew.dose)

fun averageExtractionRatio(brews: List<BrewRecord>): Double? =
    brews.mapNotNull(::extractionRatio).takeIf { it.isNotEmpty() }?.average()

fun favoriteBrewMethod(brews: List<BrewRecord>): BrewMethod? =
    brews.groupingBy { it.method }.eachCount().maxByOrNull { it.value }?.key

fun filterBrews(brews: List<BrewRecord>, method: BrewMethod? = null, from: LocalDate? = null, to: LocalDate? = null, coffeeId: Long? = null): List<BrewRecord> =
    brews.filter { brew ->
        (method == null || brew.method == method) &&
            (from == null || brew.date >= from) && (to == null || brew.date <= to) &&
            (coffeeId == null || brew.coffeeStockId == coffeeId)
    }

fun brewsByMonth(brews: List<BrewRecord>): Map<String, Int> =
    brews.groupingBy { it.date.toString().take(7) }.eachCount()

fun ratingDistribution(brews: List<BrewRecord>, ratings: Map<Long, Int>): Map<Int, Int> =
    brews.mapNotNull { ratings[it.coffeeStockId] }.groupingBy { it }.eachCount()

fun exportCsv(stock: List<CoffeeStock>, brews: List<BrewRecord>): String = buildString {
    appendLine("type,id,coffeeStockId,name,roaster,date,method,dose,yield,brewTime,notes")
    stock.forEach { item ->
        appendLine(listOf("stock", item.id, "", csvEscape(item.name), csvEscape(item.roaster), item.roastDate, "", "", "", "", csvEscape(item.tastingNotes)).joinToString(","))
    }
    brews.forEach { brew ->
        appendLine(listOf("brew", brew.id, brew.coffeeStockId, "", "", brew.date, brew.method.name, brew.dose, brew.yield ?: "", brew.brewTime, csvEscape(brew.notes)).joinToString(","))
    }
}

fun csvEscape(value: String?): String = "\"${(value ?: "").replace("\"", "\"\"")}\""

fun exportJson(stock: List<CoffeeStock>, brews: List<BrewRecord>, timestamp: String): String = buildString {
    append("{\"timestamp\":\"").append(timestamp).append("\",\"stock\":[")
    stock.forEachIndexed { index, item ->
        if (index > 0) append(',')
        append("{\"id\":").append(item.id).append(",\"name\":").append(json(item.name))
            .append(",\"roaster\":").append(json(item.roaster)).append(",\"roastDate\":").append(json(item.roastDate.toString())).append('}')
    }
    append("],\"brews\":[")
    brews.forEachIndexed { index, brew ->
        if (index > 0) append(',')
        append("{\"id\":").append(brew.id).append(",\"coffeeStockId\":").append(brew.coffeeStockId)
            .append(",\"date\":").append(json(brew.date.toString())).append(",\"method\":").append(json(brew.method.name))
            .append(",\"dose\":").append(brew.dose).append(",\"brewTime\":").append(brew.brewTime).append('}')
    }
    append("]}")
}

private fun json(value: String): String = jsonEscape(value).let { "\"$it\"" }
private fun jsonEscape(value: String): String = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

const val ESPRESSO_SECONDS = 30
const val POUR_OVER_SECONDS = 210
const val FRENCH_PRESS_SECONDS = 240
const val AEROPRESS_SECONDS = 120

fun defaultTimerSeconds(method: BrewMethod): Int = when (method) {
    BrewMethod.ESPRESSO -> ESPRESSO_SECONDS
    BrewMethod.POUR_OVER -> POUR_OVER_SECONDS
    BrewMethod.FRENCH_PRESS -> FRENCH_PRESS_SECONDS
    BrewMethod.AEROPRESS -> AEROPRESS_SECONDS
    BrewMethod.MOKA_POT -> 300
    BrewMethod.COLD_BREW -> 43200
    BrewMethod.DRIP -> 300
    BrewMethod.OTHER -> 180
}
