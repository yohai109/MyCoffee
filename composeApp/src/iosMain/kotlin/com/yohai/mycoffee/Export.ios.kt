package com.yohai.mycoffee

import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSUTF8StringEncoding

actual fun saveExportFile(filename: String, content: String): Boolean {
    val directory = NSSearchPathForDirectoriesInDomains(NSSearchPathDirectory.NSDocumentDirectory, NSUserDomainMask, true).firstOrNull() as? String ?: return false
    return runCatching { ("$directory/$filename").let { path -> content.writeToFile(path, true, NSUTF8StringEncoding, null) } }.getOrDefault(false)
}
