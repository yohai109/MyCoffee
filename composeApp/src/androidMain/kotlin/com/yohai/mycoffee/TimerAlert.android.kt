package com.yohai.mycoffee

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

actual fun notifyTimerComplete() {
    val vibrator = exportContextForSharing?.getSystemService(Vibrator::class.java) ?: return
    if (Build.VERSION.SDK_INT >= 26) vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE)) else @Suppress("DEPRECATION") vibrator.vibrate(400)
}
