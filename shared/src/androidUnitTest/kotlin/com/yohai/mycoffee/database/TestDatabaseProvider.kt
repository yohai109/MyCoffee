package com.yohai.mycoffee.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider

actual class TestDatabaseFactory actual constructor() : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CoffeeDatabase::class.java
        )
    }
}
