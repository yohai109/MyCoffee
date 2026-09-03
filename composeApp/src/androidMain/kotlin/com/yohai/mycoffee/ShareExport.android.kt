package com.yohai.mycoffee

import android.content.Intent

actual fun shareExportFile(filename: String, content: String): Boolean {
    val context = exportContextForSharing ?: return false
    return runCatching {
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = if (filename.endsWith(".json")) "application/json" else "text/csv"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_TITLE, filename)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }, "Share export").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    }.getOrDefault(false)
}
