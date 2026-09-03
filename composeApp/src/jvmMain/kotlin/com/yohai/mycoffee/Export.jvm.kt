package com.yohai.mycoffee

import java.io.File

actual fun saveExportFile(filename: String, content: String): Boolean = runCatching {
    File(System.getProperty("user.home"), "Downloads").apply { mkdirs() }.resolve(filename).writeText(content)
}.isSuccess
