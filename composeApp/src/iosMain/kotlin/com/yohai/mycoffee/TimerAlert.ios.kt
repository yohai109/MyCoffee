package com.yohai.mycoffee

import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate

actual fun notifyTimerComplete() {
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
}
