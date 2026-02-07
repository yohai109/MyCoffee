package com.yohai.mycoffee

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform