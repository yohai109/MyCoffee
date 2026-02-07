package com.yohai.mycoffee.database

import androidx.room.Room
import androidx.room.RoomDatabase

actual class TestDatabaseFactory actual constructor() : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        return Room.databaseBuilder<CoffeeDatabase>(
            name = ":memory:"
        )
    }
}
