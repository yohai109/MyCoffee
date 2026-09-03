package com.yohai.mycoffee.ui

enum class WindowLayoutClass { COMPACT, MEDIUM, EXPANDED }

fun windowLayoutClassForWidth(widthDp: Int): WindowLayoutClass = when {
    widthDp < 600 -> WindowLayoutClass.COMPACT
    widthDp < 840 -> WindowLayoutClass.MEDIUM
    else -> WindowLayoutClass.EXPANDED
}
