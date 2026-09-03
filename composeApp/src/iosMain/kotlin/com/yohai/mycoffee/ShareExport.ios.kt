package com.yohai.mycoffee

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual fun shareExportFile(filename: String, content: String): Boolean {
    val controller = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return false
    controller.presentViewController(UIActivityViewController(listOf(content), null), true, null)
    return true
}
