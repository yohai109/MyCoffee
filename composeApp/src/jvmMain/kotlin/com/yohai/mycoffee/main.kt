package com.yohai.mycoffee

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yohai.mycoffee.database.JvmDatabaseFactory
import com.yohai.mycoffee.database.initDatabase

fun main() = application {
    initDatabase(JvmDatabaseFactory())
    Window(
        onCloseRequest = ::exitApplication,
        title = "MyCoffee",
    ) {
        App()
    }
}