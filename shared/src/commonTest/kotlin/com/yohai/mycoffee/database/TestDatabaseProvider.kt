package com.yohai.mycoffee.database

import androidx.room.RoomDatabase

expect class TestDatabaseFactory() : DatabaseFactory

fun initTestDatabase() {
    initDatabase(TestDatabaseFactory())
}
