package com.yohai.mycoffee

import androidx.compose.ui.window.ComposeUIViewController
import com.yohai.mycoffee.database.IosDatabaseFactory
import com.yohai.mycoffee.database.initDatabase

fun MainViewController() = ComposeUIViewController {
    initDatabase(IosDatabaseFactory())
    App()
}