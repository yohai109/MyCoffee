package com.yohai.mycoffee

import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeStock

fun csvEscape(value: String?) = "\"${(value ?: "").replace("\"", "\"\"")}\""
fun exportCsv(stock: List<CoffeeStock>, brews: List<BrewRecord>) = buildString {
    appendLine("type,id,coffeeStockId,name,roaster,date,method,dose,yield,brewTime,notes")
    stock.forEach { appendLine(listOf("stock", it.id, "", csvEscape(it.name), csvEscape(it.roaster), it.roastDate, "", "", "", "", csvEscape(it.tastingNotes)).joinToString(",")) }
    brews.forEach { appendLine(listOf("brew", it.id, it.coffeeStockId, "", "", it.date, it.method.name, it.dose, it.yield ?: "", it.brewTime, csvEscape(it.notes)).joinToString(",")) }
}

fun exportJson(stock: List<CoffeeStock>, brews: List<BrewRecord>, timestamp: String) = buildString {
    fun q(value: String) = "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""
    append("{\"timestamp\":").append(q(timestamp)).append(",\"stock\":[")
    stock.forEachIndexed { i, item -> if (i > 0) append(','); append("{\"id\":${item.id},\"name\":${q(item.name)},\"roaster\":${q(item.roaster)},\"roastDate\":${q(item.roastDate.toString())}}") }
    append("],\"brews\":[")
    brews.forEachIndexed { i, item -> if (i > 0) append(','); append("{\"id\":${item.id},\"coffeeStockId\":${item.coffeeStockId},\"date\":${q(item.date.toString())},\"method\":${q(item.method.name)},\"dose\":${item.dose},\"yield\":${item.yield ?: "null"},\"brewTime\":${item.brewTime},\"notes\":${item.notes?.let(::q) ?: "null"}}") }
    append("]}")
}
