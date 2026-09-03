package com.yohai.mycoffee

import android.os.Environment
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import java.io.File

var exportContextForSharing: Context? = null

fun initExportContext(context: Context) {
    exportContextForSharing = context.applicationContext
}

actual fun saveExportFile(filename: String, content: String): Boolean = runCatching {
    val context = exportContextForSharing
    if (context != null && android.os.Build.VERSION.SDK_INT >= 29) {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, if (filename.endsWith(".json")) "application/json" else "text/csv")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return@runCatching false
        context.contentResolver.openOutputStream(uri)?.use { it.write(content.encodeToByteArray()) }
        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        context.contentResolver.update(uri, values, null, null) > 0
    } else {
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename).writeText(content)
        true
    }
}.getOrDefault(false)
